package de.lucaspape.cleanhdmi

import android.os.Handler
import com.sony.scalar.hardware.avio.DisplayManager
import de.lucaspape.cleanhdmi.Logger.error
import de.lucaspape.cleanhdmi.Logger.info


/**
 * Created by jonas on 2/18/17.
 */
class Display internal constructor(private val displayManager: DisplayManager) {
    var autoOffDelay = 0
        private set
    private val turnOffRunnableHandler = Handler()
    private val turnOffRunnable = Runnable { off() }

    fun turnAutoOff(delay: Int) {
        autoOffDelay = delay
        if (delay == 0) {
            turnOffRunnableHandler.removeCallbacks(turnOffRunnable)
        }
        if (delay > 0) {
            turnOffRunnableHandler.postDelayed(turnOffRunnable, delay.toLong())
        }
    }

    fun off() {
        try {
            info("turn display off")
            displayManager.switchDisplayOutputTo(DisplayManager.DEVICE_ID_NONE)
        } catch (e: Exception) {
            error("avioDisplayManager.switchDisplayOutputTo(currentOutput);")
            error(e.message!!)
            displayManager.switchDisplayOutputTo(DisplayManager.DEVICE_ID_PANEL)
        }
    }

    fun on() {
        info("turn display on")
        displayManager.switchDisplayOutputTo(DisplayManager.DEVICE_ID_PANEL)
    }

    fun on(autoOff: Boolean) {
        info("turn display on")
        displayManager.switchDisplayOutputTo(DisplayManager.DEVICE_ID_PANEL)
        if (autoOff && autoOffDelay > 0) {
            turnOffRunnableHandler.postDelayed(turnOffRunnable, autoOffDelay.toLong())
        }
    }

    companion object {
        const val NO_AUTO_OFF = 0
    }

}