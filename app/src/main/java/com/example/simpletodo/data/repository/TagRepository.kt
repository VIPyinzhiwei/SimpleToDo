package com.example.simpletodo.data.repository

import com.example.simpletodo.data.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun getAllTags(): Flow<List<Tag>>
    suspend fun addTag(tag: Tag): Long
    suspend fun updateTag(tag: Tag)
    suspend fun deleteTag(tag: Tag)
    suspend fun getTagById(tagId: Long): Tag?
} 