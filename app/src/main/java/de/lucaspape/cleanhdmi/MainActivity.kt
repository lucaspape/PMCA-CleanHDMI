package de.lucaspape.cleanhdmi

import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.github.ma1co.openmemories.framework.DisplayManager
import com.sony.scalar.hardware.CameraEx
import com.sony.scalar.hardware.CameraEx.AutoPictureReviewControl
import de.lucaspape.cleanhdmi.Logger.error
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

class MainActivity: BaseActivity(), SurfaceHolder.Callback{
    private var surfaceHolder: SurfaceHolder? = null
    private var camera: CameraEx? = null
    private var autoReviewControl: AutoPictureReviewControl? = null
    private var display:Display? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            val sw = StringWriter()
            sw.append(throwable.toString())
            sw.append("\n")
            throwable.printStackTrace(PrintWriter(sw))
            error(sw.toString())
            exitProcess(0)
        }

        setContentView(R.layout.activity_camera)

        val surfaceView = findViewById(R.id.surfaceView) as SurfaceView
        surfaceHolder = surfaceView.holder
        surfaceHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun onResume() {
        super.onResume()
        camera = CameraEx.open(0, null)
        autoReviewControl = AutoPictureReviewControl()
        camera?.setAutoPictureReviewControl(autoReviewControl)

        val params = camera?.normalCamera?.parameters
        val modifier = camera?.createParametersModifier(params)

        modifier?.driveMode = CameraEx.ParametersModifier.DRIVE_MODE_SINGLE
        modifier?.autoExposureLock = CameraEx.ParametersModifier.AE_LOCK_SPOT

        sonyDisplayManager?.let {
            display = Display(it)
            display?.on()
            display?.turnAutoOff(Display.NO_AUTO_OFF)
        }

        surfaceHolder?.addCallback(this)
    }

    override fun onPause() {
        super.onPause()
        autoReviewControl = null
        camera?.release()
        camera = null
        surfaceHolder?.removeCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        try {
            camera?.normalCamera?.setPreviewDisplay(holder)
            camera?.normalCamera?.startPreview()
        } catch (e: IOException) {
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}

    override fun surfaceDestroyed(p0: SurfaceHolder?) {}

    override fun onFocusKeyDown(): Boolean {
        camera?.normalCamera?.autoFocus(null)
        return true
    }

    override fun onFocusKeyUp():Boolean{
        camera?.normalCamera?.cancelAutoFocus()
        return true
    }

    override fun onShutterKeyDown(): Boolean {
        camera?.normalCamera?.takePicture(null, null, null)
        return true
    }

    override fun onShutterKeyUp(): Boolean {
        camera?.cancelTakePicture()
        return true
    }

    override fun setColorDepth(highQuality: Boolean) {
        super.setColorDepth(false)
    }

    override fun onMenuKeyDown():Boolean{
        exitProcess(0)
    }
}