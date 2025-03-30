package pl.wsei.pam.lab01.lab06.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import pl.wsei.pam.lab01.lab06.Priority
import pl.wsei.pam.lab01.lab06.TodoTask
import pl.wsei.pam.lab01.lab06.data.CurrentDateProvider
import pl.wsei.pam.lab01.lab06.data.database.LocalDateConverter
import pl.wsei.pam.lab01.lab06.data.repository.TodoTaskRepository
import java.time.LocalDate

class FormViewModel(
    private val repository: TodoTaskRepository,
    private val currentDateProvider: CurrentDateProvider
) : ViewModel() {

    var todoTaskUiState by mutableStateOf(TodoTaskUiState())
        private set

    suspend fun save(): Boolean {
        if (validateForSave()) {
            repository.insertItem(todoTaskUiState.todoTask.toTodoTask())
            return true
        }
        return false
    }

    fun updateUiState(todoTaskForm: TodoTaskForm) {
        todoTaskUiState = TodoTaskUiState(
            todoTask = todoTaskForm,
            isValid = validate(todoTaskForm),
            validationErrors = getValidationErrors(todoTaskForm)
        )
    }

    private fun validate(uiState: TodoTaskForm = todoTaskUiState.todoTask): Boolean {
        return with(uiState) {
            // Basic validation that title is not blank
            val isTitleValid = title.isNotBlank()

            // Validate that deadline is after or equal to current date
            val deadlineDate = LocalDateConverter.fromMillis(deadline)
            val isDeadlineValid = deadlineDate.isAfter(currentDateProvider.currentDate) ||
                    deadlineDate.isEqual(currentDateProvider.currentDate)

            isTitleValid && isDeadlineValid
        }
    }

    // Additional method for saving that requires all validations to pass
    private fun validateForSave(): Boolean {
        return validate() && todoTaskUiState.validationErrors.isEmpty()
    }

    private fun getValidationErrors(uiState: TodoTaskForm = todoTaskUiState.todoTask): List<String> {
        val errors = mutableListOf<String>()

        if (uiState.title.isBlank()) {
            errors.add("Title cannot be empty")
        }

        val deadlineDate = LocalDateConverter.fromMillis(uiState.deadline)
        if (deadlineDate.isBefore(currentDateProvider.currentDate)) {
            errors.add("Deadline cannot be in the past")
        }

        return errors
    }
}

data class TodoTaskUiState(
    var todoTask: TodoTaskForm = TodoTaskForm(),
    val isValid: Boolean = false,
    val validationErrors: List<String> = emptyList()
)

data class TodoTaskForm(
    val id: Int = 0,
    val title: String = "",
    val deadline: Long = LocalDateConverter.toMillis(LocalDate.now()),
    val isDone: Boolean = false,
    val priority: String = Priority.Low.name
)

fun TodoTask.toTodoTaskUiState(isValid: Boolean = false): TodoTaskUiState = TodoTaskUiState(
    todoTask = this.toTodoTaskForm(),
    isValid = isValid
)

fun TodoTaskForm.toTodoTask(): TodoTask = TodoTask(
    id = id.toString(),
    title = title,
    deadline = LocalDateConverter.fromMillis(deadline),
    isDone = isDone,
    priority = Priority.valueOf(priority)
)

fun TodoTask.toTodoTaskForm(): TodoTaskForm = TodoTaskForm(
    id = id.toIntOrNull() ?: 0,
    title = title,
    deadline = LocalDateConverter.toMillis(deadline),
    isDone = isDone,
    priority = priority.name
)