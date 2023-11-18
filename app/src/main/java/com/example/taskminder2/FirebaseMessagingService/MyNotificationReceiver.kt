package com.example.taskminder2.FirebaseMessagingService

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.taskminder2.HomeActivity
import com.example.taskminder2.R
import com.example.taskminder2.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val taskId = intent?.getStringExtra("taskId")

        if (context != null && taskId != null) {
            fetchTaskAndShowNotification(context, taskId)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag", "MissingPermission")
    private fun fetchTaskAndShowNotification(context: Context, taskId: String) {
        val taskRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("task")

        // Fetch the latest data from Firebase based on taskId
        taskRef.child(taskId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val task = snapshot.getValue(Task::class.java)
                    if (task != null) {
                        showNotification(context, task.title)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error as needed
            }
        })
    }

    @SuppressLint("UnspecifiedImmutableFlag", "MissingPermission")
    private fun showNotification(context: Context, title: String) {
        val channelId = "Channel_Id" // Adjust notification channel ID
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(context, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val user = context.getString(R.string.hello_user)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(user)
            .setContentText("Ingat Kamu Punya Tugas : $title")
            .setSound(defaultSoundUri)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel", NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}