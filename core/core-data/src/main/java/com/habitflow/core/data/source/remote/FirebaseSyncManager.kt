package com.habitflow.core.data.source.remote

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.habitflow.domain.model.Habit
import com.habitflow.domain.model.ReminderType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

interface HabitRemoteSync {
    val isAvailable: Boolean
    fun startListening(scope: CoroutineScope, userId: String, onRemoteHabits: suspend (List<Habit>) -> Unit)
    suspend fun upsertHabit(userId: String?, habit: Habit)
    suspend fun deleteHabit(userId: String?, habitId: String)
    fun clear()
}

class FirebaseSyncManager(context: Context) : HabitRemoteSync {

    private val firebaseApp = FirebaseApp.initializeApp(context)
        ?: FirebaseApp.getApps(context).firstOrNull()
    private val firestore = firebaseApp?.let { FirebaseFirestore.getInstance(it) }

    override val isAvailable: Boolean = firestore != null

    private var listener: ListenerRegistration? = null
    private var currentUserId: String? = null

    override fun startListening(scope: CoroutineScope, userId: String, onRemoteHabits: suspend (List<Habit>) -> Unit) {
        if (!isAvailable) return
        currentUserId = userId
        scope.launch(Dispatchers.IO) {
            listener?.remove()
            listener = habitsCollection(userId)?.addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val habits = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val name = data["name"] as? String ?: return@mapNotNull null
                    val createdAt = data["createdAt"] as? String ?: return@mapNotNull null
                    val reminderType = (data["reminderType"] as? String)
                        ?.let { runCatching { ReminderType.valueOf(it) }.getOrNull() }
                        ?: ReminderType.DAILY
                    val reminderTime = data["reminderTime"] as? String
                    val weeklyDay = (data["weeklyDay"] as? Number)?.toInt()
                    val customDateTime = data["customDateTime"] as? String
                    val historyMap = (data["history"] as? Map<*, *>)
                        ?.mapNotNull { entry ->
                            val key = entry.key as? String
                            val value = entry.value as? Boolean
                            if (key != null && value != null) key to value else null
                        }
                        ?.toMap()
                        .orEmpty()
                    Habit(
                        id = doc.id,
                        name = name,
                        createdAt = createdAt,
                        history = historyMap,
                        reminderType = reminderType,
                        reminderTime = reminderTime,
                        weeklyDay = weeklyDay,
                        customDateTime = customDateTime
                    )
                }
                scope.launch { onRemoteHabits(habits) }
            }
        }
    }

    override suspend fun upsertHabit(userId: String?, habit: Habit) {
        val uid = userId ?: currentUserId ?: return
        if (!isAvailable) return
        habitsCollection(uid)?.document(habit.id)?.set(habit.toRemoteMap())?.await()
    }

    override suspend fun deleteHabit(userId: String?, habitId: String) {
        val uid = userId ?: currentUserId ?: return
        if (!isAvailable) return
        habitsCollection(uid)?.document(habitId)?.delete()?.await()
    }

    override fun clear() {
        listener?.remove()
        listener = null
        currentUserId = null
    }

    private fun habitsCollection(userId: String) = firestore?.collection(USERS_COLLECTION)?.document(userId)?.collection(HABITS_COLLECTION)

    private fun Habit.toRemoteMap(): Map<String, Any?> = buildMap {
        put("name", name)
        put("createdAt", createdAt)
        put("reminderType", reminderType.name)
        reminderTime?.let { put("reminderTime", it) }
        weeklyDay?.let { put("weeklyDay", it) }
        customDateTime?.let { put("customDateTime", it) }
        put("history", history.filterValues { it })
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val HABITS_COLLECTION = "habits"
    }
}
