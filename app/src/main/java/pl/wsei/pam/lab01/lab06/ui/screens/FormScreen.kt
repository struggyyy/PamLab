package pl.wsei.pam.lab01.lab06.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pl.wsei.pam.lab01.lab06.ui.components.AppTopBar
import pl.wsei.pam.lab01.lab06.ui.components.TodoTaskInputBody
import pl.wsei.pam.lab01.lab06.ui.viewmodels.AppViewModelProvider
import pl.wsei.pam.lab01.lab06.ui.viewmodels.FormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    navController: NavController,
    viewModel: FormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show validation errors if any
    LaunchedEffect(viewModel.todoTaskUiState.validationErrors) {
        val errors = viewModel.todoTaskUiState.validationErrors
        if (errors.isNotEmpty()) {
            snackbarHostState.showSnackbar(message = errors.first())
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            AppTopBar(
                navController = navController,
                title = "Add Task",
                showBackIcon = true,
                route = "list",
                onSaveClick = {
                    coroutineScope.launch {
                        val success = viewModel.save()
                        if (success) {
                            navController.navigate("list")
                        } else {
                            // If validation failed, show the first error
                            val errors = viewModel.todoTaskUiState.validationErrors
                            if (errors.isNotEmpty()) {
                                snackbarHostState.showSnackbar(message = errors.first())
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        TodoTaskInputBody(
            todoUiState = viewModel.todoTaskUiState,
            onItemValueChange = viewModel::updateUiState,
            modifier = Modifier.padding(padding)
        )
    }
}