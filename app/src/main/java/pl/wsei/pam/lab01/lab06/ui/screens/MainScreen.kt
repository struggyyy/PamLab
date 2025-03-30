package pl.wsei.pam.lab01.lab06.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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