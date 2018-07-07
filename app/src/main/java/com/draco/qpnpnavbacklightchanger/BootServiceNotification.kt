package com.draco.qpnpnavbacklightchanger

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat

class BootServiceNotification : Service() {

    private val CHANNEL_ID = "my_channel_01"
    private lateinit var channel: NotificationChannel
    private lateinit var notificationManager: NotificationManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun displayNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            channel = NotificationChannel(CHANNEL_ID,
            "QPNP Channel",
            NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Initializing")
                    .setContentText("QPNP is applying your settings.").build()
            startForeground(1, notification)
        }
    }

    private fun dismissNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            notificationManager.deleteNotificationChannel(CHANNEL_ID)
        }
    }

    private fun setupOpts() {
        val prefs = getSharedPreferences("qpnpnavbacklightchanger", Context.MODE_PRIVATE)
        val setBrightness = prefs.getInt("set_brightness", 10)
        val setMaxBrightness = prefs.getInt("set_max_brightness", 10)
        val setTrigger = prefs.getString("set_trigger", "none")

        backlight.setBrightness(setBrightness)
        backlight.setMaxBrightness(setMaxBrightness)
        backlight.setTrigger(setTrigger)

        dismissNotification()

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        root = Root(applicationContext)
        backlight = Backlight()

        displayNotification()
        setupOpts()

        return START_NOT_STICKY
    }
}