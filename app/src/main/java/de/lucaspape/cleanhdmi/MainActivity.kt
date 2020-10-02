package de.lucaspape.cleanhdmi

import android.hardware.Camera
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
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

        runCommand("bk.elf r 0x01070a47")
        runCommand("bk.elf w 0x01070a47 00")
        runCommand("bk.elf r 0x01070a47")
    }

    fun runCommand(command:String){
        val su = Runtime.getRuntime().exec("su")
        val suOutputStream = su.outputStream

        suOutputStream.write(command.toByteArray())
        suOutputStream.flush()
        su.waitFor()
    }

    override fun onResume() {
        super.onResume()
        camera = CameraEx.open(0, null)
        autoReviewControl = AutoPictureReviewControl()
        camera?.setAutoPictureReviewControl(autoReviewControl)

        setSceneMode(CameraEx.ParametersModifier.SCENE_MODE_AUTO_WO_SR)

        sonyDisplayManager?.let {
            display = Display(it)
            display?.on()
            display?.turnAutoOff(Display.NO_AUTO_OFF)
        }

        surfaceHolder?.addCallback(this)
    }

    private fun setSceneMode(mode: String) {
        camera?.let {
            val params: Camera.Parameters = it.createEmptyParameters()
            params.sceneMode = mode
            camera?.normalCamera?.parameters = params
        }
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

    override fun onDownKeyDown(): Boolean {
        camera?.incrementShutterSpeed()
        return true
    }

    override fun onUpKeyDown(): Boolean {
        camera?.decrementShutterSpeed()

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

    override fun onRightKeyDown(): Boolean {
        camera?.incrementAperture()
        return true
    }

    override fun onLeftKeyDown(): Boolean {
        camera?.decrementAperture()
        return true
    }

    private var iso = 0
        set(value) {
            camera?.let {
                val params = it.createEmptyParameters()
                it.createParametersModifier(params).isoSensitivity = iso
                camera?.normalCamera?.parameters = params
            }
            field = value
        }

    override fun onUpperDialChanged(value: Int): Boolean {
        if(value > 0){
                iso+=100
        }else{
            if(iso >= 100) {
                iso -= 100
            }

        }

        return true
    }

    override fun setColorDepth(highQuality: Boolean) {
        super.setColorDepth(true)
    }
}