package com.example.simpletodo.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletodo.data.model.Priority
import com.example.simpletodo.data.model.Tag
import com.example.simpletodo.data.model.Todo
import com.example.simpletodo.data.model.TodoWithTag
import com.example.simpletodo.data.repository.TagRepository
import com.example.simpletodo.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingWorkPolicy
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import androidx.work.Data
import com.example.simpletodo.worker.ReminderWorker

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val tagRepository: TagRepository,
    private val workManager: WorkManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _showCompleted = MutableStateFlow(false)
    val showCompleted: StateFlow<Boolean> = _showCompleted

    private val _selectedTagId = MutableStateFlow<Long?>(null)
    val selectedTagId: StateFlow<Long?> = _selectedTagId

    private val _todos = MutableStateFlow<List<TodoWithTag>>(emptyList())
    val todos: StateFlow<List<TodoWithTag>> = _todos

    val tags: StateFlow<List<Tag>> = tagRepository.getAllTags()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    init {
        viewModelScope.launch {
            combine(
                todoRepository.getUncompletedTodos(),
                todoRepository.getCompletedTodos(),
                _showCompleted,
                _selectedTagId,
                _searchQuery
            ) { uncompleted, completed, showCompleted, selectedTagId, query ->
                val allTodos = if (showCompleted) uncompleted + completed else uncompleted
                allTodos
                    .filter { todoWithTag ->
                        (selectedTagId == null || todoWithTag.todo.tagId == selectedTagId) &&
                        (query.isEmpty() || todoWithTag.todo.title.contains(query, ignoreCase = true))
                    }
            }.collect { todos ->
                _todos.value = todos
            }
        }
    }

    fun toggleShowCompleted() {
        _showCompleted.value = !_showCompleted.value
    }

    fun selectTag(tagId: Long?) {
        _selectedTagId.value = tagId
    }

    fun addTodo(
        title: String,
        note: String?,
        tagId: Long?,
        priority: Priority,
        dueDate: LocalDateTime?,
        enableReminder: Boolean
    ) {
        viewModelScope.launch {
            val todo = Todo(
                title = title,
                note = note,
                tagId = tagId,
                priority = priority,
                dueDate = dueDate,
                enableReminder = enableReminder
            )
            val todoId = todoRepository.addTodo(todo)
            
            if (enableReminder && dueDate != null) {
                scheduleReminder(todoId, title, dueDate)
            }
        }
    }

    private fun scheduleReminder(todoId: Long, title: String, dueDate: LocalDateTime) {
        val reminderWork = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(workDataOf(
                "todo_id" to todoId,
                "title" to title
            ))
            .setInitialDelay(
                Duration.between(LocalDateTime.now(), dueDate).toMillis(),
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniqueWork(
            "reminder_$todoId",
            ExistingWorkPolicy.REPLACE,
            reminderWork
        )
    }

    fun addTag(name: String, color: Long) {
        viewModelScope.launch {
            tagRepository.addTag(Tag(name = name, color = color))
        }
    }

    fun toggleTodo(todo: TodoWithTag) {
        viewModelScope.launch {
            todoRepository.updateTodo(todo.todo.copy(isCompleted = !todo.todo.isCompleted))
        }
    }

    fun updateTodoTag(todo: TodoWithTag, tagId: Long?) {
        viewModelScope.launch {
            todoRepository.updateTodo(todo.todo.copy(tagId = tagId))
        }
    }

    fun deleteTodo(todo: TodoWithTag) {
        viewModelScope.launch {
            todoRepository.deleteTodo(todo.todo)
        }
    }

    fun deleteTag(tag: Tag) {
        viewModelScope.launch {
            // 先清除使用此标签的所有任务标签
            todos.value
                .filter { it.todo.tagId == tag.id }
                .forEach { todo ->
                    todoRepository.updateTodo(todo.todo.copy(tagId = null))
                }
            // 然后删除标签
            tagRepository.deleteTag(tag)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateTodoPriority(todo: TodoWithTag, priority: Priority) {
        viewModelScope.launch {
            todoRepository.updateTodo(todo.todo.copy(priority = priority))
        }
    }

    fun updateTodoDueDate(todo: TodoWithTag, dueDate: LocalDateTime?, enableReminder: Boolean) {
        viewModelScope.launch {
            // 如果之前有提醒,先取消
            if (todo.todo.enableReminder) {
                workManager.cancelUniqueWork("reminder_${todo.todo.id}")
            }
            
            // 更新任务
            val updatedTodo = todo.todo.copy(
                dueDate = dueDate,
                enableReminder = enableReminder
            )
            todoRepository.updateTodo(updatedTodo)
            
            // 如果需要设置新的提醒
            if (enableReminder && dueDate != null) {
                scheduleReminder(todo.todo.id, todo.todo.title, dueDate)
            }
        }
    }

    fun cancelReminder(todo: TodoWithTag) {
        viewModelScope.launch {
            if (todo.todo.enableReminder) {
                workManager.cancelUniqueWork("reminder_${todo.todo.id}")
                todoRepository.updateTodo(todo.todo.copy(enableReminder = false))
            }
        }
    }

    fun updateTodoNote(todoWithTag: TodoWithTag, note: String?) {
        viewModelScope.launch {
            todoRepository.updateTodo(todoWithTag.todo.copy(note = note))
        }
    }

    fun updateTodoTitle(todo: TodoWithTag, title: String) {
        viewModelScope.launch {
            if (title.isNotBlank()) {
                todoRepository.updateTodo(todo.todo.copy(title = title))
            }
        }
    }

    fun updateTodo(
        todo: TodoWithTag,
        title: String,
        note: String?,
        tagId: Long?,
        priority: Priority,
        dueDate: LocalDateTime?,
        enableReminder: Boolean
    ) {
        viewModelScope.launch {
            try {
                // 如果之前有提醒,先取消
                if (todo.todo.enableReminder) {
                    workManager.cancelUniqueWork("reminder_${todo.todo.id}")
                }
                
                // 创建更新后的 todo 对象
                val updatedTodo = todo.todo.copy(
                    title = title,
                    note = note,
                    tagId = tagId,
                    priority = priority,
                    dueDate = dueDate,
                    enableReminder = enableReminder
                )
                
                // 执行更新
                todoRepository.updateTodo(updatedTodo)
                
                // 如果需要设置新的提醒
                if (enableReminder && dueDate != null) {
                    scheduleReminder(todo.todo.id, title, dueDate)
                }
            } catch (e: Exception) {
                // 处理错误,可以添加错误状态通知
                _errorState.value = e.message ?: "更新失败"
            }
        }
    }
} 