package pl.wsei.pam.lab01.lab06.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.wsei.pam.lab01.lab06.Priority
import pl.wsei.pam.lab01.lab06.TodoTask
import java.time.LocalDate

@Entity(tableName = "tasks")
data class TodoTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val deadline: LocalDate,
    var isDone: Boolean,
    val priority: Priority
) {
    fun toModel(): TodoTask {
        return TodoTask(
            id = id.toString(),
            deadline = deadline,
            isDone = isDone,
            priority = priority,
            title = title
        )
    }

    companion object {
        fun fromModel(model: TodoTask): TodoTaskEntity {
            return TodoTaskEntity(
                id = model.id.toIntOrNull() ?: 0,
                title = model.title,
                priority = model.priority,
                isDone = model.isDone,
                deadline = model.deadline
            )
        }
    }
}