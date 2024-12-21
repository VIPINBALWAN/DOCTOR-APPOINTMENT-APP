package com.example.doctorsappointment

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(context, AppointmentActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle("Doctors Appointment")
            .setContentText("Your appointment is scheduled.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(AppointmentActivity.NOTIFICATION_ID, builder.build())
    }
}