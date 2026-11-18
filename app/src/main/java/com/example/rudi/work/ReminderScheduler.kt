package com.example.rudi.work

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import androidx.work.workDataOf
import com.example.rudi.data.model.PickupRequest
import com.example.rudi.ui.viewmodel.nairobiZone
import com.example.rudi.ui.viewmodel.pickupWindow
import java.time.Duration
import java.time.Instant
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    private fun name(id: Long) = "pickup_reminder_$id"

    suspend fun schedule(context: Context, request: PickupRequest, now: Instant = Instant.now()) {
        val start = pickupWindow(request.pickupDate, request.timeSlot)
            ?.first?.atZone(nairobiZone)?.toInstant()
        if (start == null || !start.isAfter(now)) {
            cancel(context, request.id)
            return
        }
        val target = start.minus(Duration.ofHours(24))
        val delay = if (target.isAfter(now)) Duration.between(now, target) else Duration.ofMinutes(1)
        val work = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(ReminderWorker.KEY_ID to request.id, ReminderWorker.KEY_START to start.toEpochMilli()))
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            name(request.id), ExistingWorkPolicy.REPLACE, work
        ).await()
    }

    suspend fun cancel(context: Context, id: Long) {
        WorkManager.getInstance(context).cancelUniqueWork(name(id)).await()
        context.getSystemService(android.app.NotificationManager::class.java)
            .cancel(name(id), ReminderWorker.NOTIFICATION_ID)
    }

    fun notificationTag(id: Long) = name(id)
}
