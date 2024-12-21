package com.example.simpletodo.data.model

enum class Priority(val value: Int, val color: Long) {
    LOW(0, 0xFF8BC34A),    // 绿色
    MEDIUM(1, 0xFFFFC107), // 黄色 
    HIGH(2, 0xFFE91E63);   // 红色

    companion object {
        fun fromValue(value: Int) = values().find { it.value == value } ?: LOW
    }
} 