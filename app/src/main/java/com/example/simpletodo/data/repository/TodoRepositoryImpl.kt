package com.example.simpletodo.data.repository

import com.example.simpletodo.data.local.TodoDao
import com.example.simpletodo.data.model.Todo
import com.example.simpletodo.data.model.TodoWithTag
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao
) : TodoRepository {
    override fun getAllTodos(): Flow<List<TodoWithTag>> = todoDao.getAllTodosWithTags()
    
    override fun getUncompletedTodos(): Flow<List<TodoWithTag>> = todoDao.getUncompletedTodosWithTags()
    
    override fun getCompletedTodos(): Flow<List<TodoWithTag>> = todoDao.getCompletedTodosWithTags()
    
    override fun getTodosByTag(tagId: Long): Flow<List<TodoWithTag>> = todoDao.getTodosByTagWithTags(tagId)
    
    override suspend fun addTodo(todo: Todo): Long = todoDao.insertTodo(todo)
    
    override suspend fun updateTodo(todo: Todo) = todoDao.updateTodo(todo)
    
    override suspend fun deleteTodo(todo: Todo) = todoDao.deleteTodo(todo)
} 