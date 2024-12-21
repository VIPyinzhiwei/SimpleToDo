package com.example.simpletodo.data.repository

import com.example.simpletodo.data.model.Todo
import com.example.simpletodo.data.model.TodoWithTag
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getAllTodos(): Flow<List<TodoWithTag>>
    fun getUncompletedTodos(): Flow<List<TodoWithTag>>
    fun getCompletedTodos(): Flow<List<TodoWithTag>>
    fun getTodosByTag(tagId: Long): Flow<List<TodoWithTag>>
    suspend fun addTodo(todo: Todo): Long
    suspend fun updateTodo(todo: Todo)
    suspend fun deleteTodo(todo: Todo)
} 