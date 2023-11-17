package com.zcba.megazoom.ui

import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Bundle
import android.widget.SeekBar
import com.blankj.utilcode.util.LogUtils
import com.otaliastudios.cameraview.CameraException
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraLogger
import com.otaliastudios.cameraview.CameraOptions
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor
import com.zcba.megazoom.R
import com.zcba.megazoom.app.BaseActivity
import com.zcba.megazoom.databinding.CameraActivityBinding
import java.io.ByteArrayOutputStream

class CameraActivity : BaseActivity<CameraActivityBinding>() {

    companion object {
        private const val USE_FRAME_PROCESSOR = false
        private const val DECODE_BITMAP = false
    }

    private val camera: CameraView by lazy { findViewById(R.id.camera) }
    private var captureTime: Long = 0

    override fun onBuildBinding(): CameraActivityBinding {
        return CameraActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE)
        camera.setLifecycleOwner(this)
        camera.addCameraListener(Listener())
        if (USE_FRAME_PROCESSOR) {
            camera.addFrameProcessor(object : FrameProcessor {
                private var lastTime = System.currentTimeMillis()
                override fun process(frame: Frame) {
                    val newTime = frame.time
                    val delay = newTime - lastTime
                    lastTime = newTime
                    LogUtils.w("Frame delayMillis:", delay, "FPS:", 1000 / delay)
                    if (DECODE_BITMAP) {
                        if (frame.format == ImageFormat.NV21
                            && frame.dataClass == ByteArray::class.java
                        ) {
                            val data = frame.getData<ByteArray>()
                            val yuvImage = YuvImage(
                                data,
                                frame.format,
                                frame.size.width,
                                frame.size.height,
                                null
                            )
                            val jpegStream = ByteArrayOutputStream()
                            yuvImage.compressToJpeg(
                                Rect(
                                    0, 0,
                                    frame.size.width,
                                    frame.size.height
                                ), 100, jpegStream
                            )
                            val jpegByteArray = jpegStream.toByteArray()
                            val bitmap = BitmapFactory.decodeByteArray(
                                jpegByteArray,
                                0, jpegByteArray.size
                            )
                            bitmap.toString()
                        }
                    }
                }
            })
        }

        if (camera.flash == Flash.TORCH) {
            binding.btnLight.setImageResource(R.mipmap.ic_lignt_selected)
        } else {
            camera.flash = Flash.OFF
            binding.btnLight.setImageResource(R.mipmap.ic_lignt_normal)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnCapture.setOnClickListener {
            capturePicture()
        }

        binding.btnLight.setOnClickListener {
            if (camera.flash == Flash.OFF) {
                camera.flash = Flash.TORCH
                binding.btnLight.setImageResource(R.mipmap.ic_lignt_selected)
            } else {
                camera.flash = Flash.OFF
                binding.btnLight.setImageResource(R.mipmap.ic_lignt_normal)
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                touchToggleZoom(p1.toFloat())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        binding.btnAdd.setOnClickListener {
            toggleZoom(true)
        }

        binding.btnReduce.setOnClickListener {
            toggleZoom(false)
        }
    }

    private fun touchToggleZoom(value: Float) {
        var scaleFloat = ((value) / 100).toFloat()
        binding.camera.zoom = scaleFloat
    }

    private fun toggleZoom(add: Boolean) {
        var scaleFloat = camera.zoom
        if (add) {
            if (scaleFloat < 1) {
                scaleFloat += 0.1f
            }
        } else {
            if (scaleFloat > 0.1) {
                scaleFloat -= 0.1f
            }
        }

        if (scaleFloat < 0) {
            scaleFloat = 0.0f
        } else if (scaleFloat > 1) {
            scaleFloat = 1.0f
        }
        LogUtils.d("PicturePreviewActivity", "scaleFloat:$scaleFloat")
        camera.zoom = scaleFloat
        binding.seekBar.progress = ((scaleFloat * 100).toInt())
    }

    private fun message(content: String, important: Boolean = false) {
    }

    private inner class Listener : CameraListener() {
        override fun onCameraOpened(options: CameraOptions) {
        }

        override fun onCameraError(exception: CameraException) {
            super.onCameraError(exception)
            message("Got CameraException #" + exception.reason, true)
        }

        override fun onPictureTaken(result: PictureResult) {
            super.onPictureTaken(result)
            if (camera.isTakingVideo) {
                message("Captured while taking video. Size=" + result.size, false)
                return
            }

            // This can happen if picture was taken with a gesture.
            val callbackTime = System.currentTimeMillis()
            if (captureTime == 0L) captureTime = callbackTime - 300
            LogUtils.e("onPictureTaken called! Launching activity. Delay:", callbackTime - captureTime)
            PreviewActivity.pictureResult = result
            val intent = Intent(this@CameraActivity, PreviewActivity::class.java)
            intent.putExtra("delay", callbackTime - captureTime)
            startActivity(intent)
            captureTime = 0
            message("onPictureTaken called! Launched activity.")
        }

        override fun onVideoTaken(result: VideoResult) {
            super.onVideoTaken(result)
        }

        override fun onVideoRecordingStart() {
            super.onVideoRecordingStart()
            message("onVideoRecordingStart!")
        }

        override fun onVideoRecordingEnd() {
            super.onVideoRecordingEnd()
            message("Video taken. Processing...", false)
            message("onVideoRecordingEnd!")
        }

        override fun onExposureCorrectionChanged(newValue: Float, bounds: FloatArray, fingers: Array<PointF>?) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers)
            message("Exposure correction:$newValue", false)
        }

        override fun onZoomChanged(newValue: Float, bounds: FloatArray, fingers: Array<PointF>?) {
            super.onZoomChanged(newValue, bounds, fingers)
            message("Zoom:$newValue", false)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun capturePicture() {
        if (camera.mode == Mode.VIDEO) return run {
            message("Can't take HQ pictures while in VIDEO mode.", false)
        }
        if (camera.isTakingPicture) return
        captureTime = System.currentTimeMillis()
        message("Capturing picture...", false)
        camera.takePicture()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val valid = grantResults.all { it == PERMISSION_GRANTED }
        if (valid && !camera.isOpened) {
            camera.open()
        }
    }
}