package pl.wsei.pam.lab01.lab06.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.wsei.pam.lab01.lab06.TodoTask
import pl.wsei.pam.lab01.lab06.data.repository.TodoTaskRepository

class ListViewModel(private val repository: TodoTaskRepository) : ViewModel() {
    val listUiState: StateFlow<ListUiState>
        get() {
            return repository.getAllAsStream().map { ListUiState(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = ListUiState()
                )
        }

    fun updateTask(task: TodoTask) {
        viewModelScope.launch {
            repository.updateItem(task)
        }
    }

    fun deleteTask(task: TodoTask) {
        viewModelScope.launch {
            repository.deleteItem(task)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ListUiState(val items: List<TodoTask> = listOf())