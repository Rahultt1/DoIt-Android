package com.example.todolist.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.todolist.dataSource.TaskDataSource
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.model.Task
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { TaskListAdapter() }
    private val dataSource by lazy { TaskDataSource(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerTask.adapter = adapter

        insertListeners()

        // Observe changes in the database and update the list
        lifecycleScope.launch {
            dataSource.getAllTasks().collect { tasks ->
                upadteList(tasks)
            }
        }
    }

    private fun insertListeners() {
        binding.fab.setOnClickListener {
            startActivityForResult(Intent(this, AddTaskActivity::class.java), CREATE_NEW_TASK)
        }

        adapter.listenerEdit = { task ->
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra(AddTaskActivity.TASK_ID, task.id)
            startActivityForResult(intent, CREATE_NEW_TASK)
        }

        adapter.listenerDelete = { task ->
            dataSource.deleteTask(task)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_NEW_TASK && resultCode == Activity.RESULT_OK) {
            // Refresh the list after adding/editing a task
            lifecycleScope.launch {
                dataSource.getAllTasks().collect { tasks ->
                    upadteList(tasks)
                }
            }
        }
    }

    private fun upadteList(list: List<Task>) {
        if (list.isEmpty()) {
            binding.emptyInclude.stateEmptyCs.visibility = View.VISIBLE
        } else {
            binding.emptyInclude.stateEmptyCs.visibility = View.GONE
        }
        adapter.submitList(list)
    }

    companion object {
        private const val CREATE_NEW_TASK = 1000
    }
}
