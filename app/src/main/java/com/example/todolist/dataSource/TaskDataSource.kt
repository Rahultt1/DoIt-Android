package com.example.todolist.dataSource

import android.content.Context
import com.example.todolist.database.AppDatabase
import com.example.todolist.database.TaskDao
import com.example.todolist.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskDataSource(context: Context) {
    private val _taskDao = AppDatabase.getDatabase(context).taskDao()
    // Make taskDao accessible from outside the class
    val taskDao: TaskDao get() = _taskDao  // Public getter for taskDao

    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    fun insertTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.insertTask(task)
        }
    }

    fun deleteTask(task: Task) {
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.deleteTask(task)
        }
    }
}