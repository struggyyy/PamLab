package pl.wsei.pam.lab01.lab06

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import pl.wsei.pam.lab01.lab06.data.AppContainer
import pl.wsei.pam.lab01.lab06.notification.NotificationHandler.Companion.CHANNEL_ID
import pl.wsei.pam.lab01.lab06.ui.screens.MainScreen
import pl.wsei.pam.lab01.ui.theme.Lab01Theme

class Lab06Activity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create notification channel
        createNotificationChannel()

        // Initialize container in companion object
        Lab06Activity.container = (this.application as TodoApplication).container


        setContent {
            Lab01Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(application as TodoApplication)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "TodoApp channel"
        val descriptionText = "Channel for notifications about approaching tasks"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        lateinit var container: AppContainer
    }
}