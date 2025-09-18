package com.habitflow.core.data.repository.impl

import com.habitflow.core.database.dao.RoutineProfileDao
import com.habitflow.core.database.entity.RoutineProfileEntity
import com.habitflow.domain.model.ExerciseSlot
import com.habitflow.domain.model.MealWindow
import com.habitflow.domain.model.QuietHours
import com.habitflow.domain.model.RoutineProfile
import com.habitflow.domain.model.TimeBlock
import com.habitflow.domain.repository.RoutineProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DefaultRoutineProfileRepository @Inject constructor(
    private val dao: RoutineProfileDao,
) : RoutineProfileRepository {

    override fun observeProfile(): Flow<RoutineProfile?> =
        dao.observe().map { it?.toDomain() }

    override suspend fun saveProfile(profile: RoutineProfile) {
        dao.upsert(profile.toEntity())
    }

    private fun RoutineProfileEntity.toDomain(): RoutineProfile {
        return RoutineProfile(
            wakeTime = wakeTime,
            sleepTime = sleepTime,
            workBlocks = Json.decodeFromString(workBlocksJson),
            mealWindows = Json.decodeFromString(mealWindowsJson),
            exerciseSlots = Json.decodeFromString(exerciseSlotsJson),
            quietHours = QuietHours(quietStart, quietEnd),
            hydrationGoal = hydrationGoal,
            isNightShift = isNightShift,
            goals = Json.decodeFromString(goalsJson)
        )
    }

    private fun RoutineProfile.toEntity(): RoutineProfileEntity {
        return RoutineProfileEntity(
            wakeTime = wakeTime,
            sleepTime = sleepTime,
            workBlocksJson = Json.encodeToString<List<TimeBlock>>(workBlocks),
            mealWindowsJson = Json.encodeToString<List<MealWindow>>(mealWindows),
            exerciseSlotsJson = Json.encodeToString<List<ExerciseSlot>>(exerciseSlots),
            quietStart = quietHours.start,
            quietEnd = quietHours.end,
            hydrationGoal = hydrationGoal,
            isNightShift = isNightShift,
            goalsJson = Json.encodeToString<List<String>>(goals)
        )
    }
}

