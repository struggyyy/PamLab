package pl.wsei.pam.lab01.lab06

import java.time.LocalDate

enum class Priority {
    High, Medium, Low
}

data class TodoTask(
    val id: String = "0",
    val title: String,
    val deadline: LocalDate,
    val isDone: Boolean = false,
    val priority: Priority = Priority.Medium
)