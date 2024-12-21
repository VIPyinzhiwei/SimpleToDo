package com.example.simpletodo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val note: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val tagId: Long? = null,
    val priority: Priority = Priority.LOW,
    val dueDate: LocalDateTime? = null,
    val enableReminder: Boolean = false
) 