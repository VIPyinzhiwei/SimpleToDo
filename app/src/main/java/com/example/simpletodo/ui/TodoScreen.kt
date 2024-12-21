@file:Suppress("EXPERIMENTAL_MATERIAL3_API")

package com.example.simpletodo.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.simpletodo.R
import com.example.simpletodo.data.model.Priority
import com.example.simpletodo.data.model.Tag
import com.example.simpletodo.data.model.TodoWithTag
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TodoScreen(
    viewModel: TodoViewModel
) {
    var showSearch by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val todos by viewModel.todos.collectAsStateWithLifecycle()
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val showCompleted by viewModel.showCompleted.collectAsStateWithLifecycle()
    val selectedTagId by viewModel.selectedTagId.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showAddTagDialog by remember { mutableStateOf(false) }
    var isMultiSelectMode by remember { mutableStateOf(false) }
    var selectedTodos by remember { mutableStateOf(setOf<TodoWithTag>()) }
    var showMultiDeleteConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    if (isMultiSelectMode) {
                        Text("已选择 ${selectedTodos.size} 项")
                    } else if (showSearch) {
                        TextField(
                            value = searchQuery,
                            onValueChange = viewModel::updateSearchQuery,
                            placeholder = { Text("搜索任务...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(stringResource(R.string.app_name))
                    }
                },
                navigationIcon = {
                    if (isMultiSelectMode) {
                        IconButton(
                            onClick = {
                                isMultiSelectMode = false
                                selectedTodos = emptySet()
                            }
                        ) {
                            Icon(Icons.Default.Close, "退出多选")
                        }
                    }
                },
                actions = {
                    if (isMultiSelectMode) {
                        IconButton(
                            onClick = { showMultiDeleteConfirmDialog = true }
                        ) {
                            Icon(Icons.Default.Delete, "批量删除")
                        }
                    } else {
                        IconButton(
                            onClick = {
                                showSearch = !showSearch
                                if (!showSearch) viewModel.updateSearchQuery("")
                            }
                        ) {
                            Icon(
                                imageVector = if (showSearch) {
                                    Icons.Default.Close
                                } else {
                                    Icons.Default.Search
                                },
                                contentDescription = if (showSearch) {
                                    "关闭搜索"
                                } else {
                                    "搜索"
                                }
                            )
                        }
                        Switch(
                            checked = showCompleted,
                            onCheckedChange = { viewModel.toggleShowCompleted() }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isMultiSelectMode) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Todo")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 标签列表
            TagList(
                tags = tags,
                selectedTagId = selectedTagId,
                onTagSelected = viewModel::selectTag,
                onAddTagClick = { showAddTagDialog = true },
                onDeleteTag = { viewModel.deleteTag(it) }
            )

            // 待办事项列表
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = todos.isEmpty(),
                    transitionSpec = {
                        fadeIn() + slideInVertically() togetherWith fadeOut() + slideOutVertically()
                    }
                ) { isEmpty ->
                    if (isEmpty) {
                        EmptyState(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center)
                        )
                    } else {
                        TodoList(
                            todos = todos,
                            tags = tags,
                            isMultiSelectMode = isMultiSelectMode,
                            selectedTodos = selectedTodos,
                            onToggleTodo = viewModel::toggleTodo,
                            onDeleteTodo = viewModel::deleteTodo,
                            onUpdateTodo = viewModel::updateTodo,
                            onCancelReminder = viewModel::cancelReminder,
                            onStartMultiSelect = { todo ->
                                isMultiSelectMode = true
                                selectedTodos = setOf(todo)
                            },
                            onSelectTodo = { todo, selected ->
                                if (selected) {
                                    selectedTodos += todo
                                } else {
                                    selectedTodos -= todo
                                }
                            }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddTodoDialog(
                tags = tags,
                onDismiss = { showAddDialog = false },
                onConfirm = { title, note, tagId, priority, dueDate, enableReminder ->
                    viewModel.addTodo(title, note, tagId, priority, dueDate, enableReminder)
                    showAddDialog = false
                }
            )
        }

        if (showAddTagDialog) {
            AddTagDialog(
                onDismiss = { showAddTagDialog = false },
                onConfirm = { name, color ->
                    viewModel.addTag(name, color)
                    showAddTagDialog = false
                }
            )
        }

        // 添加批量删除确认对话框
        if (showMultiDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showMultiDeleteConfirmDialog = false },
                title = { Text("确认删除") },
                text = { Text("确定要删除选中的 ${selectedTodos.size} 个任务吗？") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedTodos.forEach { viewModel.deleteTodo(it) }
                            isMultiSelectMode = false
                            selectedTodos = emptySet()
                            showMultiDeleteConfirmDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("删除")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showMultiDeleteConfirmDialog = false }
                    ) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TagList(
    tags: List<Tag>,
    selectedTagId: Long?,
    onTagSelected: (Long?) -> Unit,
    onAddTagClick: () -> Unit,
    onDeleteTag: (Tag) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDeleteConfirmDialog by remember { mutableStateOf<Tag?>(null) }

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            FilterChip(
                selected = selectedTagId == null,
                onClick = { onTagSelected(null) },
                label = { Text("全部") }
            )
        }

        items(tags) { tag ->
            Surface(
                modifier = Modifier.combinedClickable(
                    onClick = { onTagSelected(tag.id) },
                    onLongClick = { showDeleteConfirmDialog = tag }
                ),
                shape = MaterialTheme.shapes.small,
                color = if (selectedTagId == tag.id) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
                border = BorderStroke(
                    1.dp,
                    if (selectedTagId == tag.id) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(tag.color))
                    )
                    Text(tag.name)
                }
            }
        }

        item {
            OutlinedIconButton(
                onClick = onAddTagClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Tag",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // 删除确认对话框
    showDeleteConfirmDialog?.let { tag ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("删除标签") },
            text = { Text("确定要删除标签 ${tag.name} 吗？\n删除后，使用该标签的任务将变为无标签。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTag(tag)
                        showDeleteConfirmDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun TodoList(
    todos: List<TodoWithTag>,
    tags: List<Tag>,
    isMultiSelectMode: Boolean,
    selectedTodos: Set<TodoWithTag>,
    onToggleTodo: (TodoWithTag) -> Unit,
    onDeleteTodo: (TodoWithTag) -> Unit,
    onUpdateTodo: (TodoWithTag, String, String?, Long?, Priority, LocalDateTime?, Boolean) -> Unit,
    onCancelReminder: (TodoWithTag) -> Unit,
    onStartMultiSelect: (TodoWithTag) -> Unit,
    onSelectTodo: (TodoWithTag, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(todos) { todoWithTag ->
            TodoItem(
                todoWithTag = todoWithTag,
                tags = tags,
                isMultiSelectMode = isMultiSelectMode,
                isSelected = selectedTodos.contains(todoWithTag),
                onToggle = { onToggleTodo(todoWithTag) },
                onDelete = { onDeleteTodo(todoWithTag) },
                onUpdateTodo = onUpdateTodo,
                onCancelReminder = onCancelReminder,
                onLongClick = { onStartMultiSelect(todoWithTag) },
                onSelectChange = { selected -> onSelectTodo(todoWithTag, selected) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TodoItem(
    todoWithTag: TodoWithTag,
    tags: List<Tag>,
    isMultiSelectMode: Boolean,
    isSelected: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onUpdateTodo: (TodoWithTag, String, String?, Long?, Priority, LocalDateTime?, Boolean) -> Unit,
    onCancelReminder: (TodoWithTag) -> Unit,
    onLongClick: () -> Unit,
    onSelectChange: (Boolean) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var isNoteExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .combinedClickable(
                onClick = {
                    if (isMultiSelectMode) {
                        onSelectChange(!isSelected)
                    } else {
                        showEditDialog = true
                    }
                },
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 优先级指示器
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(todoWithTag.todo.priority.color))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Checkbox(
                        checked = todoWithTag.todo.isCompleted,
                        onCheckedChange = { onToggle() }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = todoWithTag.todo.title,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = if (todoWithTag.todo.isCompleted) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )
                }
            }

            // 标签显示
            if (todoWithTag.tag != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    modifier = Modifier.padding(start = 40.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = Color(todoWithTag.tag.color).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = todoWithTag.tag.name,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(todoWithTag.tag.color)
                    )
                }
            }

            // 修改截止日期显示部分
            if (todoWithTag.todo.dueDate != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 40.dp)
                ) {
                    Icon(
                        imageVector = if (todoWithTag.todo.enableReminder) {
                            Icons.Default.Alarm
                        } else {
                            Icons.Default.Schedule
                        },
                        contentDescription = "截止日期",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatDueDate(todoWithTag.todo.dueDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isOverdue(todoWithTag.todo.dueDate)) {
                            Color.Red
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )

                    if (todoWithTag.todo.enableReminder) {
                        IconButton(
                            onClick = { onCancelReminder(todoWithTag) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsOff,
                                contentDescription = "取消提醒",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // 修改备注显示部分
            if (!todoWithTag.todo.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier.padding(start = 40.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = todoWithTag.todo.note,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = if (isNoteExpanded) Int.MAX_VALUE else 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        // 只保留展开/收起按钮
                        if (todoWithTag.todo.note.lines().size > 2) {
                            TextButton(
                                onClick = { isNoteExpanded = !isNoteExpanded },
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Text(
                                    text = if (isNoteExpanded) "收起" else "展开",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 编辑对话���
    if (showEditDialog) {
        EditTodoDialog(
            todoWithTag = todoWithTag,
            tags = tags,
            onDismiss = { showEditDialog = false },
            onConfirm = { title, note, tagId, priority, dueDate, enableReminder ->
                onUpdateTodo(
                    todoWithTag,
                    title,
                    note,
                    tagId,
                    priority,
                    dueDate,
                    enableReminder
                )
                showEditDialog = false
            },
            onDelete = {
                onDelete()
                showEditDialog = false
            }
        )
    }
}

// 格式化截止日期显示
private fun formatDueDate(dateTime: LocalDateTime): String {
    val now = LocalDateTime.now()
    val days = ChronoUnit.DAYS.between(now.toLocalDate(), dateTime.toLocalDate())

    return when {
        days == 0L -> "今天 ${dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        days == 1L -> "明天 ${dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        days == -1L -> "昨天 ${dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        days > 0 -> dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))
        else -> "已过期 ${dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))}"
    }
}

// 检查是否过期
private fun isOverdue(dateTime: LocalDateTime): Boolean {
    return dateTime.isBefore(LocalDateTime.now())
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "你还没有待办事项",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "点击右下角按钮加吧！",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialog(
    tags: List<Tag>,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, Long?, Priority, LocalDateTime?, Boolean) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedTagId by remember { mutableStateOf<Long?>(null) }
    var selectedPriority by remember { mutableStateOf(Priority.LOW) }
    var dueDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var enableReminder by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加新任务") },
        text = {
            Column {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("请输入任务内容") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 添加备注输入框
                TextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = { Text("添加备注(可选)") },
                    modifier = Modifier.height(100.dp),
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "优先级",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Priority.values().forEach { priority ->
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            label = {
                                Text(
                                    when (priority) {
                                        Priority.LOW -> "低"
                                        Priority.MEDIUM -> "中"
                                        Priority.HIGH -> "高"
                                    }
                                )
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color(priority.color))
                                )
                            }
                        )
                    }
                }

                Text(
                    text = "选择标签",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTagId == null,
                        onClick = { selectedTagId = null },
                        label = { Text("无标签") }
                    )
                    tags.forEach { tag ->
                        FilterChip(
                            selected = selectedTagId == tag.id,
                            onClick = { selectedTagId = tag.id },
                            label = { Text(tag.name) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color(tag.color))
                                )
                            }
                        )
                    }
                }

                // 添加截止日期选择
                Text(
                    text = "截止日期",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val currentDueDate = dueDate // 存储到本地变量
                    Text(
                        text = if (currentDueDate != null) {
                            currentDueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        } else {
                            "未设置"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "选择日期"
                            )
                        }
                        if (currentDueDate != null) {
                            IconButton(onClick = { dueDate = null }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "清除日期"
                                )
                            }
                        }
                    }
                }

                // 添加提醒开关
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "开启提醒",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = enableReminder,
                        onCheckedChange = { enableReminder = it },
                        enabled = dueDate != null
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        text,
                        note.ifBlank { null },
                        selectedTagId,
                        selectedPriority,
                        dueDate,
                        enableReminder
                    )
                },
                enabled = text.isNotBlank()
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )

    // 期选择器
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { date ->
                showDatePicker = false
                showTimePicker = true
                dueDate = LocalDateTime.of(date, LocalTime.now())
            },
            onRemove = {
                dueDate = null
                enableReminder = false
            }
        )
    }

    if (showTimePicker) {
        val currentDueDate = dueDate // 存储到本地变量
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = { time ->
                showTimePicker = false
                if (currentDueDate != null) {
                    dueDate = currentDueDate.with(time)
                }
            }
        )
    }
}

// 加日期选择器对话框
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onRemove: () -> Unit = {}
) {
    val datePickerState = rememberDatePickerState()

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(date)
                    }
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onRemove) {
                    Text("删除")
                }
                TextButton(onClick = onDismissRequest) {
                    Text("取消")
                }
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

// 添加时间选择器对话框
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    val timePickerState = rememberTimePickerState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                val time = LocalTime.of(
                    timePickerState.hour,
                    timePickerState.minute
                )
                onTimeSelected(time)
            }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("取消")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTagDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val primaryColor = MaterialTheme.colorScheme.primary // 先提取 colorScheme
    var selectedColor by remember {
        mutableStateOf(primaryColor.toArgb().toLong()) // 使用 remember 保存状态
    }
    val colors = remember {
        listOf(
            Color(0xFF1976D2), // Blue
            Color(0xFF388E3C), // Green
            Color(0xFFF57C00), // Orange
            Color(0xFFD32F2F), // Red
            Color(0xFF7B1FA2), // Purple
            Color(0xFF455A64)  // Blue Grey
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加新标签") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("请输入标签名称") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "选择颜色",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable {
                                    selectedColor = color
                                        .toArgb()
                                        .toLong()
                                }
                                .then(
                                    if (selectedColor == color
                                            .toArgb()
                                            .toLong()
                                    ) {
                                        Modifier.border(
                                            2.dp,
                                            MaterialTheme.colorScheme.primary,
                                            CircleShape
                                        )
                                    } else {
                                        Modifier
                                    }
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, selectedColor) },
                enabled = name.isNotBlank()
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    initialDateTime: LocalDateTime?,
    initialEnableReminder: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (LocalDateTime?, Boolean) -> Unit
) {
    var dateTime by remember { mutableStateOf(initialDateTime) }
    var enableReminder by remember { mutableStateOf(initialEnableReminder) }
    var showDatePicker by remember { mutableStateOf(true) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var pendingDateTime by remember { mutableStateOf<LocalDateTime?>(null) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            onDateSelected = { date ->
                showDatePicker = false
                showTimePicker = true
                dateTime = LocalDateTime.of(date, LocalTime.now())
            },
            onRemove = {
                onConfirm(null, false)
            }
        )
    }

    if (showTimePicker) {
        val currentDateTime = dateTime // 存储到本地变量
        if (currentDateTime != null) {
            TimePickerDialog(
                onDismissRequest = {
                    showTimePicker = false
                    showDatePicker = true
                },
                onTimeSelected = { time ->
                    val updatedDateTime = currentDateTime.with(time)
                    pendingDateTime = updatedDateTime
                    showTimePicker = false
                    showReminderDialog = true
                }
            )
        }
    }

    if (showReminderDialog && pendingDateTime != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("设置提醒") },
            text = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("是否需要提醒?")
                    Switch(
                        checked = enableReminder,
                        onCheckedChange = { enableReminder = it }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onConfirm(pendingDateTime, enableReminder) }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("取消")
                }
            }
        )
    }
}

// 添加编辑备注对话框
@Composable
fun EditNoteDialog(
    initialNote: String?,
    onDismiss: () -> Unit,
    onConfirm: (String?) -> Unit
) {
    var note by remember { mutableStateOf(initialNote ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑备注") },
        text = {
            TextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("添加备注(可选)") },
                modifier = Modifier.height(200.dp),
                maxLines = 8
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(note.ifBlank { null }) }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun EditTodoDialog(
    todoWithTag: TodoWithTag,
    tags: List<Tag>,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, Long?, Priority, LocalDateTime?, Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var title by remember { mutableStateOf(todoWithTag.todo.title) }
    var note by remember { mutableStateOf(todoWithTag.todo.note ?: "") }
    var selectedTagId by remember { mutableStateOf(todoWithTag.todo.tagId) }
    var selectedPriority by remember { mutableStateOf(todoWithTag.todo.priority) }
    var dueDate by remember { mutableStateOf(todoWithTag.todo.dueDate) }
    var enableReminder by remember { mutableStateOf(todoWithTag.todo.enableReminder) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑任务") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("任务内容") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = { Text("添加备注(可选)") },
                    modifier = Modifier.height(100.dp),
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Priority selection
                Text("优先级", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Priority.values().forEach { priority ->
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = { selectedPriority = priority },
                            label = {
                                Text(
                                    when (priority) {
                                        Priority.LOW -> "低"
                                        Priority.MEDIUM -> "中"
                                        Priority.HIGH -> "高"
                                    }
                                )
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color(priority.color))
                                )
                            }
                        )
                    }
                }

                // Tag selection
                Text("选择标签", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTagId == null,
                        onClick = { selectedTagId = null },
                        label = { Text("无标签") }
                    )
                    tags.forEach { tag ->
                        FilterChip(
                            selected = selectedTagId == tag.id,
                            onClick = { selectedTagId = tag.id },
                            label = { Text(tag.name) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color(tag.color))
                                )
                            }
                        )
                    }
                }

                // Due date selection
                Text(
                    text = "截止日期",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (dueDate != null) {
                            dueDate!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        } else {
                            "未设置"
                        }
                    )
                    Row {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "选择日期"
                            )
                        }
                        if (dueDate != null) {
                            IconButton(onClick = { dueDate = null; enableReminder = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "清除日期"
                                )
                            }
                        }
                    }
                }

                // Reminder switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "开启提醒",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = enableReminder,
                        onCheckedChange = { enableReminder = it },
                        enabled = dueDate != null
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧放删除按钮
                TextButton(
                    onClick = { showDeleteConfirmDialog = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }

                // 右侧放取消/确定按钮
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    TextButton(
                        onClick = {
                            onConfirm(
                                title,
                                note.ifBlank { null },
                                selectedTagId,
                                selectedPriority,
                                dueDate,
                                enableReminder
                            )
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text("确定")
                    }
                }
            }
        }
    )

    // 删除确认对话框
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这个任务吗？") },
            confirmButton = {
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }

    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { date ->
                showDatePicker = false
                showTimePicker = true
                dueDate = LocalDateTime.of(date, LocalTime.now())
            },
            onRemove = {
                dueDate = null
                enableReminder = false
            }
        )
    }

    // Time picker dialog
    if (showTimePicker) {
        val currentDueDate = dueDate
        if (currentDueDate != null) {
            TimePickerDialog(
                onDismissRequest = { showTimePicker = false },
                onTimeSelected = { time ->
                    showTimePicker = false
                    dueDate = currentDueDate.with(time)
                }
            )
        }
    }
} 