package io.bitrise.androidtodokotlinandcomposesample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.bitrise.androidtodokotlinandcomposesample.ui.theme.AndroidToDoKotlinAndComposeSampleTheme

data class Task(val text: String, val isDone: Boolean)

class Tasks {
    private val tasks = mutableStateListOf<Task>()

    fun addTask(task: Task) {
        tasks.add(task)
    }

    fun updateTask(originalTask: Task, updatedTask: Task) {
        val originalIndex = tasks.indexOf(originalTask)
        tasks[originalIndex] = updatedTask
    }

    fun removeTask(task: Task) {
        tasks.remove(task)
    }

    fun getNotDoneTasks(): List<Task> {
        return tasks.filter { !it.isDone }
    }

    fun getDoneTasks(): List<Task> {
        return tasks.filter { it.isDone }
    }

    fun getAllTasks(): List<Task> {
        return tasks
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidToDoKotlinAndComposeSampleTheme {
                TodoApp()
            }
        }
    }
}

@Composable
fun TodoApp() {
    val tasks = remember { Tasks() }
    val newTask = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colors.background
        ) {
            TextField(
                value = newTask.value,
                onValueChange = { newTask.value = it },
                label = { Text("New Task") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = {
                if (newTask.value.isNotBlank()) {
                    tasks.addTask(Task(newTask.value, false))
                    newTask.value = ""
                }
            }, modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        ) {
            Text("Add Task")
        }

        val notDoneTasks = tasks.getNotDoneTasks()
        val doneTasks = tasks.getDoneTasks()

        Text("Not Done Yet", modifier = Modifier.padding(16.dp))
        LazyColumn(modifier = Modifier.weight(1f).testTag("NotDoneYetList")) {
            items(notDoneTasks.size) { index ->
                TaskItem(task = notDoneTasks[index], tasks = tasks) { updatedTask ->
                    tasks.updateTask(notDoneTasks[index], updatedTask)
                }
            }
        }

        Text("Done", modifier = Modifier.padding(16.dp))
        LazyColumn(modifier = Modifier.weight(1f).testTag("DoneList")) {
            items(doneTasks.size) { index ->
                TaskItem(task = doneTasks[index], tasks = tasks) { updatedTask ->
                    tasks.updateTask(doneTasks[index], updatedTask)
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, tasks: Tasks, onTaskUpdated: (Task) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("row-${task.text}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isDone, onCheckedChange = { isChecked ->
                onTaskUpdated(task.copy(isDone = isChecked))
            }, modifier = Modifier.padding(end = 16.dp).testTag("checkbox-${task.text}")
        )
        Text(text = task.text, modifier = Modifier.weight(1f))
        IconButton(onClick = { tasks.removeTask(task) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Task"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidToDoKotlinAndComposeSampleTheme {
        TodoApp()
    }
}
