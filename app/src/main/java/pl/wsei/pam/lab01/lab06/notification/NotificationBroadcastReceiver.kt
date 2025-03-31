package pl.wsei.pam.lab01.lab06.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import pl.wsei.pam.lab01.R
import pl.wsei.pam.lab01.lab06.notification.NotificationHandler.Companion.CHANNEL_ID
import pl.wsei.pam.lab01.lab06.notification.NotificationHandler.Companion.MESSAGE_EXTRA
import pl.wsei.pam.lab01.lab06.notification.NotificationHandler.Companion.NOTIFICATION_ID
import pl.wsei.pam.lab01.lab06.notification.NotificationHandler.Companion.TITLE_EXTRA

class NotificationBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent?.getStringExtra(TITLE_EXTRA))
            .setContentText(intent?.getStringExtra(MESSAGE_EXTRA))
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }
}