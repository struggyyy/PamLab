package pl.wsei.pam.lab01.lab06.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController,
    title: String,
    showBackIcon: Boolean = false,
    route: String = "list",
    onSaveClick: () -> Unit = { }
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
            if (route !== "form") {
                OutlinedButton(
                    onClick = onSaveClick
                ) {
                    Text(
                        text = "Save",
                        fontSize = 18.sp
                    )
                }
            } else {
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
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        }
    )
}