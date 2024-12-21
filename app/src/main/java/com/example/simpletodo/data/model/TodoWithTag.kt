package com.example.simpletodo.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class TodoWithTag(
    @Embedded
    val todo: Todo,
    
    @Relation(
        parentColumn = "tagId",
        entityColumn = "id"
    )
    val tag: Tag?
) 