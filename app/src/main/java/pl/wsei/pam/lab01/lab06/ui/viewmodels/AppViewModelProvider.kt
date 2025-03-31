package pl.wsei.pam.lab01.lab06.ui.viewmodels

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import pl.wsei.pam.lab01.lab06.TodoApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ListViewModel(
                repository = todoApplication().container.todoTaskRepository,
                taskAlarmScheduler = todoApplication().container.taskAlarmScheduler
            )
        }

        initializer {
            FormViewModel(
                repository = todoApplication().container.todoTaskRepository,
                currentDateProvider = todoApplication().container.currentDateProvider
            )
        }
    }
}

fun CreationExtras.todoApplication(): TodoApplication {
    val app = this[APPLICATION_KEY]
    return app as TodoApplication
}