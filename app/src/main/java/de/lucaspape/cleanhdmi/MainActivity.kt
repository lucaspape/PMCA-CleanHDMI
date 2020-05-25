package de.lucaspape.cleanhdmi

import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.sony.scalar.hardware.CameraEx
import de.lucaspape.cleanhdmi.Logger.error
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

class MainActivity: BaseActivity(), SurfaceHolder.Callback{
    private var surfaceHolder: SurfaceHolder? = null
    private var camera: CameraEx? = null

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

        surfaceHolder?.addCallback(this)
    }

    override fun onPause() {
        super.onPause()
        camera?.release()
        camera = null
        surfaceHolder?.removeCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        try {
            camera?.normalCamera?.setPreviewDisplay(holder)
            val formats = camera?.normalCamera?.parameters?.previewFormat
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