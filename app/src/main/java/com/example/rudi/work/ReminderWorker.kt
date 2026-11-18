package com.example.rudi.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.rudi.R
import com.example.rudi.data.local.RudiDatabase
import com.example.rudi.ui.MainActivity
import com.example.rudi.ui.slotText
import com.example.rudi.ui.title
import com.example.rudi.ui.viewmodel.nairobiZone
import com.example.rudi.ui.viewmodel.pickupWindow
import java.time.Instant

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                applicationContext, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) return Result.success()

        val id = inputData.getLong(KEY_ID, 0L)
        val dao = RudiDatabase.get(applicationContext).pickupDao()
        val request = dao.getById(id) ?: return Result.success()
        val start = pickupWindow(request.pickupDate, request.timeSlot)
            ?.first?.atZone(nairobiZone)?.toInstant() ?: return Result.success()
        if (start.toEpochMilli() != inputData.getLong(KEY_START, -1L) ||
            !start.isAfter(Instant.now()) || isStopped) return Result.success()

        val manager = applicationContext.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(CHANNEL, "Pickup reminders", NotificationManager.IMPORTANCE_DEFAULT))
        val open = PendingIntent.getActivity(
            applicationContext, 0, Intent(applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL)
            .setSmallIcon(R.drawable.ic_requests)
            .setContentTitle("Pickup coming up: " + request.title())
            .setContentText(request.slotText() + ". Confirm with your provider.")
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        if (dao.getById(id) == request && !isStopped && start.isAfter(Instant.now())) {
            manager.notify(ReminderScheduler.notificationTag(id), NOTIFICATION_ID, notification)
        }
        return Result.success()
    }

    companion object {
        const val KEY_ID = "requestId"
        const val KEY_START = "slotStart"
        const val NOTIFICATION_ID = 1
        private const val CHANNEL = "pickup_reminders"
    }
}
