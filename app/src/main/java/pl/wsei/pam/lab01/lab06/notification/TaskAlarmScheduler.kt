package pl.wsei.pam.lab01.lab06.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import pl.wsei.pam.lab01.lab06.TodoTask
import pl.wsei.pam.lab01.lab06.data.database.LocalDateConverter
import pl.wsei.pam.lab01.lab06.notification.NotificationHandler.Companion.NOTIFICATION_ID
import pl.wsei.pam.lab01.lab06.notification.NotificationHandler.Companion.TITLE_EXTRA
import pl.wsei.pam.lab01.lab06.notification.NotificationHandler.Companion.MESSAGE_EXTRA
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class TaskAlarmScheduler(private val context: Context) {

    private var currentAlarmTaskId: String? = null
    private var currentAlarmTime: Long = 0L

    fun scheduleAlarmForNextTask(tasks: List<TodoTask>) {
        // Filter tasks that aren't done and have future deadlines
        val uncompletedTasks = tasks.filter { !it.isDone && !it.deadline.isBefore(LocalDate.now()) }

        if (uncompletedTasks.isEmpty()) {
            cancelCurrentAlarm()
            return
        }

        // Find the task with nearest deadline
        val nextTask = uncompletedTasks.minByOrNull { it.deadline }

        nextTask?.let {
            // Calculate the notification time (1 day before deadline)
            val notificationDate = it.deadline.minusDays(1)
            val notificationDateTime = LocalDateTime.of(notificationDate, LocalTime.of(9, 0))
            val notificationTimeMillis = notificationDateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            // If this is a new task or has an earlier deadline than current alarm
            if (currentAlarmTaskId != it.id || notificationTimeMillis < currentAlarmTime) {
                // Cancel any existing alarm
                cancelCurrentAlarm()

                // Schedule new alarm
                scheduleAlarm(
                    notificationTimeMillis,
                    "Task Deadline Approaching",
                    "Task '${it.title}' is due tomorrow!",
                    it.title
                )

                // Store current alarm info
                currentAlarmTaskId = it.id
                currentAlarmTime = notificationTimeMillis
            }
        }
    }

    private fun scheduleAlarm(time: Long, title: String, message: String, taskTitle: String) {
        val intent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
            putExtra(TITLE_EXTRA, title)
            putExtra(MESSAGE_EXTRA, message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Schedule the alarm
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )

        // Schedule repeated alarms (every 4 hours)
        scheduleRepeatedAlarms(alarmManager, time, taskTitle)
    }

    private fun scheduleRepeatedAlarms(alarmManager: AlarmManager, startTime: Long, taskTitle: String) {
        // Schedule alarms at +4h, +8h, +12h, +16h, +20h from the original time
        val fourHoursInMillis = 4 * 60 * 60 * 1000L

        for (i in 1..5) {
            val repeatTime = startTime + (fourHoursInMillis * i)

            val repeatIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_ID + i, // Different request code for each alarm
                Intent(context, NotificationBroadcastReceiver::class.java).apply {
                    putExtra(TITLE_EXTRA, "Task Reminder")
                    putExtra(MESSAGE_EXTRA, "Task '$taskTitle' deadline is approaching!")
                },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                repeatTime,
                repeatIntent
            )
        }
    }

    private fun cancelCurrentAlarm() {
        if (currentAlarmTaskId != null) {
            val intent = Intent(context, NotificationBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
            )

            pendingIntent?.let {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(it)
                it.cancel()
            }

            // Cancel repeated alarms
            for (i in 1..5) {
                val repeatIntent = PendingIntent.getBroadcast(
                    context,
                    NOTIFICATION_ID + i,
                    Intent(context, NotificationBroadcastReceiver::class.java),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
                )

                repeatIntent?.let {
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.cancel(it)
                    it.cancel()
                }
            }

            currentAlarmTaskId = null
            currentAlarmTime = 0L
        }
    }
}