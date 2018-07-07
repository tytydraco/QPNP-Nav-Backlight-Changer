package com.draco.qpnpnavbacklightchanger

import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView

lateinit var root: Root
lateinit var runAsync: RunAsync
lateinit var backlight: Backlight

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var setBrightness: EditText
    private lateinit var setMaxBrightness: EditText
    private lateinit var setTrigger: EditText
    private lateinit var listTrigger: TextView
    private lateinit var applyOnBoot: CheckBox
    private lateinit var apply: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("qpnpnavbacklightchanger", Context.MODE_PRIVATE)
        editor = prefs.edit()

        setBrightness = findViewById(R.id.set_brightness)
        setMaxBrightness = findViewById(R.id.set_max_brightness)
        setTrigger = findViewById(R.id.set_trigger)
        listTrigger = findViewById(R.id.list_trigger)
        applyOnBoot = findViewById(R.id.apply_on_boot)
        apply = findViewById(R.id.apply)

        root = Root(this)
        runAsync = RunAsync()
        backlight = Backlight()

        val filter = IntentFilter(filter_get_current_options)
        this.registerReceiver(runAsync, filter)

        root.checkRoot()

        if (!backlight.checkCompat()) {
            notCompatDialog()
        }

        // async setup UI
        setupUI()
    }

    fun apply() {
        runAsync.runnable_async(this, Runnable {
            val setBrightness = setBrightness.text.toString().toInt()
            val setMaxBrightness = setMaxBrightness.text.toString().toInt()
            val setTrigger = setTrigger.text.toString()

            backlight.setBrightness(setBrightness)
            backlight.setMaxBrightness(setMaxBrightness)
            backlight.setTrigger(setTrigger)

            editor.putInt("set_brightness", setBrightness)
            editor.putInt("set_max_brightness", setMaxBrightness)
            editor.putString("set_trigger", setTrigger)
            editor.putBoolean("apply_on_boot", true)
            editor.apply()
        })
    }

    private fun setupUI() {
        // lock apply
        apply.isEnabled = false

        applyOnBoot.isChecked = prefs.getBoolean("apply_on_boot", false)
        apply.setOnClickListener { apply() }

        runAsync.runnable_async(this, Runnable {
            val getBrightness = backlight.getBrightness().toString()
            runOnUiThread {
                setBrightness.setText(getBrightness)
            }

            val getMaxBrightness = backlight.getMaxBrightness().toString()
            runOnUiThread {
                setMaxBrightness.setText(getMaxBrightness)
            }

            val getTrigger = backlight.getTrigger()
            runOnUiThread {
                setTrigger.setText(getTrigger)
            }

            val getListTriggers = backlight.listTriggers().toString().replace("[", "").replace(", ]", "")
            runOnUiThread {
                listTrigger.text = getListTriggers
            }

            // unlock apply
            runOnUiThread {
                apply.isEnabled = true
            }
        })
    }

    private fun notCompatDialog() {
        AlertDialog.Builder(this)
                .setTitle("Not Compatible")
                .setMessage("Your device is not compatible with this app because " +
                        "it lacks the QPNP 10 debugfs tunables.")
                .setCancelable(false)
                .setPositiveButton("Ok", { _, _ ->
                    android.os.Process.killProcess(android.os.Process.myPid())
                    System.exit(1)
                })
                .create()
                .show()
    }

    public override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(runAsync)
    }
}
