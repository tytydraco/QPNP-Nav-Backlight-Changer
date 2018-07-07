package com.draco.qpnpnavbacklightchanger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlin.concurrent.thread

val filter_get_current_options = "RUN_ASYNC"

class RunAsync : BroadcastReceiver() {

    companion object {
        lateinit var runnable: Runnable
        var finished: Boolean = true
        var ui: Boolean = true
        lateinit var handler: android.os.Handler
    }

    init {
        handler = android.os.Handler()
    }

    fun runnable_async(context: Context, runnable: Runnable, ui: Boolean = false) {
        RunAsync.runnable = runnable
        RunAsync.ui = ui

        // call get_current_objects async task
        val setup_options = Intent(filter_get_current_options)
        setup_options.flags = Intent.FLAG_RECEIVER_FOREGROUND
        context.sendBroadcast(setup_options)
    }

    override fun onReceive(context: Context, intent: Intent) {
        thread {
            finished = false
            if (ui) {
                handler.post(runnable)
            } else {
                runnable.run()
            }
            finished = true
        }
    }
}