package com.example.todolist.ui

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.todolist.dataSource.TaskDataSource
import com.example.todolist.databinding.ActivityAddTaskBinding
import com.example.todolist.extensions.format
import com.example.todolist.extensions.text
import com.example.todolist.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*
import kotlinx.coroutines.launch

class AddTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTaskBinding
    private val dataSource by lazy { TaskDataSource(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)) {
            val taskId = intent.getIntExtra(TASK_ID, 0)
            lifecycleScope.launch {
                // Use the TaskDao to find the task by ID
                val task = dataSource.taskDao.findById(taskId) // Get taskDao from dataSource
                task?.let {
                    binding.tilTitle.text = it.title
                    binding.tilDate.text = it.date
                    binding.tilTimer.text = it.hour
                }
            }
        }

        insertListeners()
    }

    private fun insertListeners() {
        binding.tilDate.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener { timestamp ->
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                binding.tilDate.text = Date(timestamp + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.tilTimer.editText?.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build()
            timePicker.addOnPositiveButtonClickListener {
                val minute = if (timePicker.minute < 10) "0${timePicker.minute}" else timePicker.minute.toString()
                val hour = if (timePicker.hour < 10) "0${timePicker.hour}" else timePicker.hour.toString()
                binding.tilTimer.text = "$hour:$minute"
            }
            timePicker.show(supportFragmentManager, null)
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }

        binding.buttonNewTask.setOnClickListener {
            val task = Task(
                title = binding.tilTitle.text,
                date = binding.tilDate.text,
                hour = binding.tilTimer.text,
                id = intent.getIntExtra(TASK_ID, 0) // 0 if it's a new task
            )
            dataSource.insertTask(task)

            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    companion object {
        const val TASK_ID = "task_id"
    }
}
