package pl.wsei.pam.lab01.lab06.ui.screens

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import pl.wsei.pam.lab01.lab06.TodoApplication

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(application: TodoApplication? = null) {
    val navController = rememberNavController()

    // Request notification permission only on API 33+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val postNotificationPermission =
            rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

        LaunchedEffect(key1 = true) {
            if (!postNotificationPermission.status.isGranted) {
                postNotificationPermission.launchPermissionRequest()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") { ListScreen(navController) }
        composable("form") { FormScreen(navController) }
    }
}
