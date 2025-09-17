package com.habitflow.core.database

import androidx.room.TypeConverter
import com.habitflow.domain.model.ReminderType

class Converters {
    @TypeConverter
    fun reminderTypeToString(type: ReminderType?): String? = type?.name

    @TypeConverter
    fun stringToReminderType(value: String?): ReminderType? = value?.let { ReminderType.valueOf(it) }
}
