package pl.wsei.pam.lab01.lab06.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pl.wsei.pam.lab01.lab06.Priority
import pl.wsei.pam.lab01.lab06.TodoTask
import java.time.format.DateTimeFormatter

@Composable
fun ListItem(
    item: TodoTask,
    onToggleDone: (TodoTask) -> Unit,
    onDelete: (TodoTask) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status checkbox - now interactive
            Checkbox(
                checked = item.isDone,
                onCheckedChange = { isChecked ->
                    onToggleDone(item.copy(isDone = isChecked))
                }
            )

            // Task details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                // Title with priority indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None
                    )

                    // Priority tag
                    val priorityColor = when (item.priority) {
                        Priority.High -> MaterialTheme.colorScheme.error
                        Priority.Medium -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.secondary
                    }

                    Text(
                        text = item.priority.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = priorityColor,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Deadline
                Text(
                    text = "Due: ${item.deadline.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (item.isDone)
                        MaterialTheme.colorScheme.outline
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Delete button
            IconButton(onClick = { onDelete(item) }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}