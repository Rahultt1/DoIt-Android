package com.example.todolist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class Task(
    val title: String,
    val hour: String,
    val date: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)
