package pl.wsei.pam.lab01.lab06

import java.time.LocalDate

enum class Priority {
    High, Medium, Low
}

data class TodoTask(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val deadline: LocalDate,
    val isDone: Boolean = false,
    val priority: Priority = Priority.Medium
)

object TodoTaskRepository {
    private val tasks = mutableListOf(
        TodoTask(
            title = "Programming",
            deadline = LocalDate.of(2024, 4, 18),
            priority = Priority.Low
        ),
        TodoTask(
            title = "Teaching",
            deadline = LocalDate.of(2024, 5, 12),
            priority = Priority.High
        ),
        TodoTask(
            title = "Learning",
            deadline = LocalDate.of(2024, 6, 28),
            isDone = true,
            priority = Priority.Low
        ),
        TodoTask(
            title = "Cooking",
            deadline = LocalDate.of(2024, 8, 18),
            priority = Priority.Medium
        )
    )

    fun getAllTasks(): List<TodoTask> = tasks.toList()

    fun addTask(task: TodoTask) {
        tasks.add(task)
    }
}