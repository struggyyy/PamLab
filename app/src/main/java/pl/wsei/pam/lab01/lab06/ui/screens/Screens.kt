package pl.wsei.pam.lab01.lab06.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pl.wsei.pam.lab01.lab06.ui.components.ListItem
import pl.wsei.pam.lab01.lab06.ui.components.AppTopBar
import pl.wsei.pam.lab01.lab06.ui.viewmodels.AppViewModelProvider
import pl.wsei.pam.lab01.lab06.ui.viewmodels.ListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navController: NavController,
    viewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val listUiState by viewModel.listUiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add task",
                        modifier = Modifier.scale(1.5f)
                    )
                },
                onClick = {
                    navController.navigate("form")
                }
            )
        },
        topBar = {
            AppTopBar(
                navController = navController,
                title = "Todo List",
                showBackIcon = false,
                route = "form"
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            ) {
                items(items = listUiState.items, key = { it.id }) { task ->
                    ListItem(
                        item = task,
                        onToggleDone = { updatedTask ->
                            viewModel.updateTask(updatedTask)
                        },
                        onDelete = { taskToDelete ->
                            viewModel.deleteTask(taskToDelete)
                        }
                    )
                }
            }
        }
    )
}