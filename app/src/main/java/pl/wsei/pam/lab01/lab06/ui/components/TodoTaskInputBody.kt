package pl.wsei.pam.lab01.lab06.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.wsei.pam.lab01.lab06.ui.viewmodels.TodoTaskForm
import pl.wsei.pam.lab01.lab06.ui.viewmodels.TodoTaskUiState

@Composable
fun TodoTaskInputBody(
    todoUiState: TodoTaskUiState,
    onItemValueChange: (TodoTaskForm) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TodoTaskInputForm(
            item = todoUiState.todoTask,
            validationErrors = todoUiState.validationErrors,
            onValueChange = onItemValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}