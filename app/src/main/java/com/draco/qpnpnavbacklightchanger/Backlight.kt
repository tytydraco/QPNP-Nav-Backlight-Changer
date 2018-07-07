package com.draco.qpnpnavbacklightchanger

class Backlight {

    private val QPNP_PATH = "/sys/bus/spmi/devices/leds-qpnp-10/leds/button-backlight"

    fun checkCompat(): Boolean {
        return root.run("if [ -d \"$QPNP_PATH\" ]; then echo 1; fi") == "1"
    }

    fun setBrightness(brightness: Int) {
        root.run("echo $brightness > $QPNP_PATH/brightness")
    }

    fun getBrightness(): Int {
        return root.run("cat $QPNP_PATH/brightness").toInt()
    }

    fun setMaxBrightness(brightness: Int) {
        root.run("echo $brightness > $QPNP_PATH/max_brightness")
    }

    fun getMaxBrightness(): Int {
        return root.run("cat $QPNP_PATH/max_brightness").toInt()
    }

    fun setTrigger(trigger: String) {
        root.run("echo $trigger > $QPNP_PATH/trigger")
    }

    fun getTrigger(): String {
        val catTrigger = root.run("cat $QPNP_PATH/trigger")

        return catTrigger.split("[")[1].split("]")[0]
    }

    fun listTriggers(): List<String> {
        val catTrigger = root.run("cat $QPNP_PATH/trigger")
        return catTrigger
                .replace("[", "")
                .replace("]", "")
                .split(" ")
    }
}