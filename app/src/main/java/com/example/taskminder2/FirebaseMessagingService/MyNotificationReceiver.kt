package com.example.taskminder2.FirebaseMessagingService

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.app.PendingIntent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.taskminder2.HomeActivity
import com.example.taskminder2.R

class MyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("title")
        val taskId = intent?.getStringExtra("taskId")

        if (title != null && taskId != null) {
            showNotification(context, title, taskId)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag", "MissingPermission")
    private fun showNotification(context: Context?, title: String, taskId: String) {
        val channelId = "Channel_Id" // Sesuaikan dengan ID kanal notifikasi Anda
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(context, HomeActivity::class.java) // Ganti dengan aktivitas yang menampilkan detail tugas
        intent.putExtra("taskId", taskId)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val user = context?.getString(R.string.hello_user)
        val notificationBuilder = NotificationCompat.Builder(context!!, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(user)
            .setContentText("Ingat Kamu Punya Tugas : $title")
            .setSound(defaultSoundUri)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel", NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}