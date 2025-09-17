package com.habitflow.core.data.repository.impl

import com.habitflow.core.data.source.remote.HabitRemoteSync
import com.habitflow.core.database.dao.HabitDao
import com.habitflow.core.database.entity.HabitCompletionEntity
import com.habitflow.core.data.mapper.toDomain
import com.habitflow.core.data.mapper.toEntity
import com.habitflow.core.data.mapper.toCompletionEntities
import com.habitflow.core.database.entity.HabitWithCompletions
import com.habitflow.core.database.entity.SettingsEntity
import com.habitflow.domain.model.Habit
import com.habitflow.domain.model.ReminderType
import com.habitflow.domain.repository.HabitRepository
import com.habitflow.domain.time.DateProvider
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import com.habitflow.core.data.di.RoomTransactionRunner
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Singleton
class DefaultHabitRepository @Inject constructor(
    private val habitDao: HabitDao,
    private val remoteSync: HabitRemoteSync,
    private val dateProvider: DateProvider,
    private val uuidProvider: () -> String,
    externalScope: CoroutineScope,
    @RoomTransactionRunner private val transactionRunner: @JvmSuppressWildcards suspend (suspend () -> Unit) -> Unit
) : HabitRepository {

    private val scope = externalScope

    private var currentUserId: String? = null

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    override val habits = _habits

    private val _remindersEnabled = MutableStateFlow(false)
    override val remindersEnabled = _remindersEnabled

    init {
        scope.launch {
            habitDao.observeHabitsWithCompletions().collectLatest { list ->
                _habits.value = list.map { it.toHabit() }
            }
        }
        scope.launch {
            habitDao.observeSettings().collectLatest { settings ->
                _remindersEnabled.value = settings?.remindersEnabled ?: false
            }
        }
    }

    override fun addHabit(
        name: String,
        reminderType: ReminderType,
        reminderTime: String?,
        weeklyDay: Int?,
        customDateTime: String?
    ): Habit {
        val habit = Habit(
            id = uuidProvider(),
            name = name,
            createdAt = isoDateNow(),
            history = emptyMap(),
            reminderType = reminderType,
            reminderTime = reminderTime,
            weeklyDay = weeklyDay,
            customDateTime = customDateTime
        )
        scope.launch {
            habitDao.insertHabit(habit.toEntity())
            remoteSync.upsertHabit(currentUserId, habit)
        }
        return habit
    }

    override fun deleteHabit(id: String) {
        scope.launch {
            habitDao.deleteHabit(id)
            habitDao.deleteCompletionsForHabit(id)
            remoteSync.deleteHabit(currentUserId, id)
        }
    }

    override fun toggleCompletion(id: String, dateKey: String, completed: Boolean) {
        scope.launch {
            if (completed) {
                habitDao.insertCompletion(HabitCompletionEntity(id, dateKey))
            } else {
                habitDao.deleteCompletion(id, dateKey)
            }
            val existing = _habits.value.firstOrNull { it.id == id }
            if (existing != null) {
                val updatedHistory = existing.history.toMutableMap()
                if (completed) {
                    updatedHistory[dateKey] = true
                } else {
                    updatedHistory.remove(dateKey)
                }
                remoteSync.upsertHabit(currentUserId, existing.copy(history = updatedHistory))
            }
        }
    }

    override fun setRemindersEnabled(enabled: Boolean) {
        _remindersEnabled.value = enabled
        scope.launch {
            habitDao.upsertSettings(SettingsEntity(remindersEnabled = enabled))
        }
    }

    private fun HabitWithCompletions.toHabit(): Habit =
        toDomain()

    private fun isoDateNow(): String =
        DateTimeFormatter.ISO_INSTANT.format(
            dateProvider.today().atStartOfDay(ZoneId.systemDefault()).toInstant()
        )

    private suspend fun persistRemoteHabits(remoteHabits: List<Habit>) {
        transactionRunner {
            habitDao.clearCompletions()
            habitDao.clearHabits()
            habitDao.insertHabits(
                remoteHabits.map { it.toEntity() }
            )
            val allCompletions = remoteHabits.flatMap { it.toCompletionEntities() }
            habitDao.insertCompletions(allCompletions)
        }
    }

    override fun onUserChanged(userId: String?) {
        currentUserId = userId
        if (!remoteSync.isAvailable) return
        if (userId == null) {
            remoteSync.clear()
            return
        }
        remoteSync.startListening(scope, userId) { remoteHabits ->
            scope.launch {
                persistRemoteHabits(remoteHabits)
            }
        }
    }
}
