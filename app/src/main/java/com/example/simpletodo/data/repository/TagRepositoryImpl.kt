package com.example.simpletodo.data.repository

import com.example.simpletodo.data.local.TagDao
import com.example.simpletodo.data.model.Tag
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {
    override fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()
    
    override suspend fun addTag(tag: Tag): Long = tagDao.insertTag(tag)
    
    override suspend fun updateTag(tag: Tag) = tagDao.updateTag(tag)
    
    override suspend fun deleteTag(tag: Tag) = tagDao.deleteTag(tag)
    
    override suspend fun getTagById(tagId: Long): Tag? = tagDao.getTagById(tagId)
} 