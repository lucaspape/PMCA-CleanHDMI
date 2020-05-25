package de.lucaspape.cleanhdmi

import android.app.Activity
import android.content.Intent
import android.view.KeyEvent
import com.github.ma1co.openmemories.framework.DateTime
import com.github.ma1co.openmemories.framework.DeviceInfo
import com.github.ma1co.openmemories.framework.DisplayManager
import com.sony.scalar.sysutil.ScalarInput

open class BaseActivity : Activity(),
    DisplayManager.Listener {
    var displayManager: DisplayManager? = null
        private set

    override fun onResume() {
        Logger.info("Resume " + componentName.className)
        super.onResume()
        displayManager = DisplayManager.create(this)

        displayManager?.addListener(this)

        setColorDepth(true)
        notifyAppInfo()
    }

    override fun onPause() {
        Logger.info("Pause " + componentName.className)
        super.onPause()
        setColorDepth(false)
        displayManager!!.release()
        displayManager = null
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return when (event.scanCode) {
            ScalarInput.ISV_KEY_UP -> onUpKeyDown()
            ScalarInput.ISV_KEY_DOWN -> onDownKeyDown()
            ScalarInput.ISV_KEY_LEFT -> onLeftKeyDown()
            ScalarInput.ISV_KEY_RIGHT -> onRightKeyDown()
            ScalarInput.ISV_KEY_ENTER -> onEnterKeyDown()
            ScalarInput.ISV_KEY_FN -> onFnKeyDown()
            ScalarInput.ISV_KEY_AEL -> onAelKeyDown()
            ScalarInput.ISV_KEY_MENU, ScalarInput.ISV_KEY_SK1 -> onMenuKeyDown()
            ScalarInput.ISV_KEY_S1_1 -> onFocusKeyDown()
            ScalarInput.ISV_KEY_S1_2 -> true
            ScalarInput.ISV_KEY_S2 -> onShutterKeyDown()
            ScalarInput.ISV_KEY_PLAY -> onPlayKeyDown()
            ScalarInput.ISV_KEY_STASTOP -> onMovieKeyDown()
            ScalarInput.ISV_KEY_CUSTOM1 -> onC1KeyDown()
            ScalarInput.ISV_KEY_DELETE, ScalarInput.ISV_KEY_SK2 -> onDeleteKeyDown()
            ScalarInput.ISV_KEY_LENS_ATTACH -> onLensAttached()
            ScalarInput.ISV_DIAL_1_CLOCKWISE, ScalarInput.ISV_DIAL_1_COUNTERCW -> onUpperDialChanged(
                getDialStatus(ScalarInput.ISV_DIAL_1_STATUS) / 22
            )
            ScalarInput.ISV_DIAL_2_CLOCKWISE, ScalarInput.ISV_DIAL_2_COUNTERCW -> onLowerDialChanged(
                getDialStatus(ScalarInput.ISV_DIAL_2_STATUS) / 22
            )
            ScalarInput.ISV_KEY_MODE_DIAL -> onModeDialChanged(getDialStatus(ScalarInput.ISV_KEY_MODE_DIAL))
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return when (event.scanCode) {
            ScalarInput.ISV_KEY_UP -> onUpKeyUp()
            ScalarInput.ISV_KEY_DOWN -> onDownKeyUp()
            ScalarInput.ISV_KEY_LEFT -> onLeftKeyUp()
            ScalarInput.ISV_KEY_RIGHT -> onRightKeyUp()
            ScalarInput.ISV_KEY_ENTER -> onEnterKeyUp()
            ScalarInput.ISV_KEY_FN -> onFnKeyUp()
            ScalarInput.ISV_KEY_AEL -> onAelKeyUp()
            ScalarInput.ISV_KEY_MENU, ScalarInput.ISV_KEY_SK1 -> onMenuKeyUp()
            ScalarInput.ISV_KEY_S1_1 -> onFocusKeyUp()
            ScalarInput.ISV_KEY_S1_2 -> true
            ScalarInput.ISV_KEY_S2 -> onShutterKeyUp()
            ScalarInput.ISV_KEY_PLAY -> onPlayKeyUp()
            ScalarInput.ISV_KEY_STASTOP -> onMovieKeyUp()
            ScalarInput.ISV_KEY_CUSTOM1 -> onC1KeyUp()
            ScalarInput.ISV_KEY_DELETE, ScalarInput.ISV_KEY_SK2 -> onDeleteKeyUp()
            ScalarInput.ISV_KEY_LENS_ATTACH -> onLensDetached()
            ScalarInput.ISV_DIAL_1_CLOCKWISE, ScalarInput.ISV_DIAL_1_COUNTERCW -> true
            ScalarInput.ISV_DIAL_2_CLOCKWISE, ScalarInput.ISV_DIAL_2_COUNTERCW -> true
            ScalarInput.ISV_KEY_MODE_DIAL -> true
            else -> super.onKeyUp(keyCode, event)
        }
    }

    protected fun getDialStatus(key: Int): Int {
        return ScalarInput.getKeyStatus(key).status
    }

    protected fun onUpKeyDown(): Boolean {
        return false
    }

    protected fun onUpKeyUp(): Boolean {
        return false
    }

    protected fun onDownKeyDown(): Boolean {
        return false
    }

    protected fun onDownKeyUp(): Boolean {
        return false
    }

    protected fun onLeftKeyDown(): Boolean {
        return false
    }

    protected fun onLeftKeyUp(): Boolean {
        return false
    }

    protected fun onRightKeyDown(): Boolean {
        return false
    }

    protected fun onRightKeyUp(): Boolean {
        return false
    }

    protected fun onEnterKeyDown(): Boolean {
        return false
    }

    protected fun onEnterKeyUp(): Boolean {
        return false
    }

    protected fun onFnKeyDown(): Boolean {
        return false
    }

    protected fun onFnKeyUp(): Boolean {
        return false
    }

    protected fun onAelKeyDown(): Boolean {
        return false
    }

    protected fun onAelKeyUp(): Boolean {
        return false
    }

    protected open fun onMenuKeyDown(): Boolean {
        return false
    }

    protected fun onMenuKeyUp(): Boolean {
        return false
    }

    protected open fun onFocusKeyDown(): Boolean {
        return false
    }

    protected open fun onFocusKeyUp(): Boolean {
        return false
    }

    protected open fun onShutterKeyDown(): Boolean {
        return false
    }

    protected open fun onShutterKeyUp(): Boolean {
        return false
    }

    protected fun onPlayKeyDown(): Boolean {
        return false
    }

    protected fun onPlayKeyUp(): Boolean {
        return false
    }

    protected fun onMovieKeyDown(): Boolean {
        return false
    }

    protected fun onMovieKeyUp(): Boolean {
        return false
    }

    protected fun onC1KeyDown(): Boolean {
        return false
    }

    protected fun onC1KeyUp(): Boolean {
        return false
    }

    protected fun onLensAttached(): Boolean {
        return false
    }

    protected fun onLensDetached(): Boolean {
        return false
    }

    protected fun onUpperDialChanged(value: Int): Boolean {
        return false
    }

    protected fun onLowerDialChanged(value: Int): Boolean {
        return false
    }

    protected fun onModeDialChanged(value: Int): Boolean {
        return false
    }

    protected fun onDeleteKeyDown(): Boolean {
        return true
    }

    protected fun onDeleteKeyUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun displayChanged(display: DisplayManager.Display) {
        AppNotificationManager()
            .notify(NOTIFICATION_DISPLAY_CHANGED)
    }

    protected fun setAutoPowerOffMode(enable: Boolean) {
        val mode = if (enable) "APO/NORMAL" else "APO/NO" // or "APO/SPECIAL" ?
        val intent = Intent()
        intent.action = "com.android.server.DAConnectionManagerService.apo"
        intent.putExtra("apo_info", mode)
        sendBroadcast(intent)
    }

    protected open fun setColorDepth(highQuality: Boolean) {
        displayManager!!.setColorDepth(if (highQuality) DisplayManager.ColorDepth.HIGH else DisplayManager.ColorDepth.LOW)
    }

    protected fun notifyAppInfo() {
        val intent = Intent("com.android.server.DAConnectionManagerService.AppInfoReceive")
        intent.putExtra("package_name", componentName.packageName)
        intent.putExtra("class_name", componentName.className)
        //intent.putExtra("pkey", new String[] {});// either this or these two:
        //intent.putExtra("pullingback_key", new String[] {});
        //intent.putExtra("resume_key", new String[] {});
        sendBroadcast(intent)
    }

    val deviceInfo: DeviceInfo
        get() = DeviceInfo.getInstance()

    val dateTime: DateTime
        get() = DateTime.getInstance()

    companion object {
        const val NOTIFICATION_DISPLAY_CHANGED = "NOTIFICATION_DISPLAY_CHANGED"
    }
}