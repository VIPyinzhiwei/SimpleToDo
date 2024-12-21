package com.example.simpletodo.data.local

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.simpletodo.data.model.Priority

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }

    @TypeConverter
    fun priorityToInt(priority: Priority): Int = priority.value

    @TypeConverter
    fun intToPriority(value: Int): Priority = Priority.fromValue(value)
} 