package pl.wsei.pam.lab01.lab06

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.wsei.pam.lab01.ui.theme.Lab01Theme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController,
    title: String,
    showBackIcon: Boolean = false,
    route: String = "list"
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (showBackIcon) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            // Home icon
            IconButton(onClick = {
                // Navigate to home/list screen
                navController.popBackStack(route, false)
            }) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home"
                )
            }

            // Settings icon
            IconButton(onClick = {
                // TODO: Implement settings screen navigation
                // navController.navigate("settings")
            }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    )
}

@Composable
fun ListItem(item: TodoTask, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Deadline: ${item.deadline.format(DateTimeFormatter.ISO_LOCAL_DATE)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Priority: ${item.priority}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Status: ${if (item.isDone) "Completed" else "Pending"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopBar(
                navController = navController,
                title = "Todo List"
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("form")
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(TodoTaskRepository.getAllTasks()) { task ->
                ListItem(item = task)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(navController: NavController) {
    // State variables to track form inputs
    var title by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf(LocalDate.now()) }
    var priority by remember { mutableStateOf(Priority.Medium) }
    var isDone by remember { mutableStateOf(false) }

    // Date picker state
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = deadline.atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    )
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                navController = navController,
                title = "Add Task",
                showBackIcon = true
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Task Title TextField
            Text("Title", style = MaterialTheme.typography.bodyMedium)
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Add Task Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Deadline Date Picker
            Text("Deadline", style = MaterialTheme.typography.bodyMedium)
            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(deadline.format(DateTimeFormatter.ISO_LOCAL_DATE))
            }

            // Date Picker Dialog
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    deadline = Instant.ofEpochMilli(it)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDatePicker = false }
                        ) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Priority Dropdown
            Text("Priority", style = MaterialTheme.typography.bodyMedium)
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = priority.name,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Priority.values().forEach { priorityOption ->
                        DropdownMenuItem(
                            text = { Text(priorityOption.name) },
                            onClick = {
                                priority = priorityOption
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Completion Status Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isDone,
                    onCheckedChange = { isDone = it }
                )
                Text("Completed", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    // Validate and save the task
                    if (title.isNotBlank()) {
                        val newTask = TodoTask(
                            title = title,
                            deadline = deadline,
                            isDone = isDone,
                            priority = priority
                        )
                        TodoTaskRepository.addTask(newTask)
                        navController.navigateUp() // Return to list screen
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Task")
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") { ListScreen(navController) }
        composable("form") { FormScreen(navController) }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Lab01Theme {
        MainScreen()
    }
}