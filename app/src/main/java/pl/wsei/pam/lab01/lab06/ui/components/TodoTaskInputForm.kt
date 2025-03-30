package pl.wsei.pam.lab01.lab06.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.wsei.pam.lab01.lab06.Priority
import pl.wsei.pam.lab01.lab06.data.database.LocalDateConverter
import pl.wsei.pam.lab01.lab06.ui.viewmodels.TodoTaskForm
import pl.wsei.pam.lab01.lab06.ui.viewmodels.TodoTaskUiState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTaskInputForm(
    item: TodoTaskForm,
    validationErrors: List<String>,
    modifier: Modifier = Modifier,
    onValueChange: (TodoTaskForm) -> Unit = {},
    enabled: Boolean = true
) {
    Text(
        text = "Title",
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
        style = MaterialTheme.typography.titleMedium
    )

    Column(modifier = modifier.padding(8.dp)) {
        TextField(
            value = item.title,
            label = { Text("Add Task Title...") },
            onValueChange = {
                onValueChange(item.copy(title = it))
            },
            isError = validationErrors.any { it.contains("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        if (validationErrors.any { it.contains("Title") }) {
            Text(
                text = "Title cannot be empty!",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        val datePickerState = rememberDatePickerState(
            initialDisplayMode = DisplayMode.Picker,
            yearRange = IntRange(2000, 2030),
            initialSelectedDateMillis = item.deadline,
        )
        var showDialog by remember {
            mutableStateOf(false)
        }

        val deadlineDate = LocalDateConverter.fromMillis(item.deadline)
        val formattedDate = deadlineDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable(onClick = {
                    showDialog = true
                }),
            text = "Deadline: $formattedDate",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            color = if (validationErrors.any { it.contains("Deadline") })
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.onSurface
        )

        if (validationErrors.any { it.contains("Deadline") }) {
            Text(
                text = "Deadline cannot be in the past!",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                textAlign = TextAlign.Center
            )
        }

        if (showDialog) {
            DatePickerDialog(
                onDismissRequest = {
                    showDialog = false
                },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        datePickerState.selectedDateMillis?.let {
                            onValueChange(item.copy(deadline = it))
                        }
                    }) {
                        Text("Pick")
                    }
                }
            ) {
                DatePicker(state = datePickerState, showModeToggle = true)
            }
        }

        // Priority selection with RadioButtons
        Text(
            text = "Priority",
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium
        )

        Column {
            Priority.values().forEach { priorityOption ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onValueChange(item.copy(priority = priorityOption.name)) }
                ) {
                    RadioButton(
                        selected = item.priority == priorityOption.name,
                        onClick = { onValueChange(item.copy(priority = priorityOption.name)) }
                    )
                    Text(
                        text = priorityOption.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Status",
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium
        )

        // Completion status checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { onValueChange(item.copy(isDone = !item.isDone)) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isDone,
                onCheckedChange = {
                    onValueChange(item.copy(isDone = it))
                }
            )
            Text(
                "Completed",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}