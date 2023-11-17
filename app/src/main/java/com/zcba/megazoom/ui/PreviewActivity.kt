package com.zcba.megazoom.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.zcba.megazoom.R
import com.zcba.megazoom.app.BaseActivity
import com.zcba.megazoom.utils.Utils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PathUtils
import com.github.chrisbanes.photoview.OnScaleChangedListener
import com.otaliastudios.cameraview.PictureResult
import com.zcba.megazoom.databinding.PreviewActivityBinding
import com.zcba.megazoom.extensions.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class PreviewActivity : BaseActivity<PreviewActivityBinding>() {

    private var saveSuccess = false

    companion object {
        var pictureResult: PictureResult? = null
    }

    override fun onBuildBinding(): PreviewActivityBinding {
        return PreviewActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val result = pictureResult ?: run {
            finish()
            return
        }
        val imageView = findViewById<ImageView>(R.id.image)

        try {
            result.toBitmap(2560, 2560) { bitmap -> imageView.setImageBitmap(bitmap) }
        } catch (e: UnsupportedOperationException) {
            imageView.setImageDrawable(ColorDrawable(Color.GREEN))
        }
        if (result.isSnapshot) {
            // Log the real size for debugging reason.
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(result.data, 0, result.data.size, options)
        }

        binding.image.minimumScale = 1f
        binding.image.maximumScale = 8.0f
        binding.image.setOnScaleChangeListener(object : OnScaleChangedListener {
            override fun onScaleChange(scaleFactor: Float, focusX: Float, focusY: Float) {
                binding.seekBar.progress = ((binding.image.scale * 100).toInt() - 100)
            }
        })

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnDownload.setOnClickListener {
            lifecycleScope.launch {
                launch {
                    disableView(false)
                    disableClick(false)
                    result.toBitmap(2560, 2560) { bitmap ->
                        if (bitmap != null) {
                            saveSuccess = true
                            saveImage(bitmap)
                            toast("The image has been successfully stored in your system's album.")
                        } else {
                            saveSuccess = false
                            disableView(true)
                            disableClick(true)
                            toast("Save failed")
                        }
                    }
                }
                launch {
                    delay(2500)
                    if (saveSuccess) {
                        startActivity(Intent(this@PreviewActivity, HomeActivity::class.java))
                        finish()
                    }
                }
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    touchToggleZoom(p1.toFloat())
                }
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
        var scaleFloat = ((value * 8) / 100).toFloat()
        if (scaleFloat < 1) {
            scaleFloat = 1f
        }
        if (scaleFloat > 8) {
            scaleFloat = 8.0f
        }
        binding.image.scale = scaleFloat
    }

    private fun toggleZoom(add: Boolean) {
        var scaleFloat = binding.image.scale
        if (add) {
            if (scaleFloat < 8) {
                scaleFloat += 1
            }
        } else {
            if (scaleFloat > 1) {
                scaleFloat -= 1
            }
        }

        if (scaleFloat < 1) {
            scaleFloat = 1f
        } else if (scaleFloat > 8) {
            scaleFloat = 8.0f
        }

        Log.d("PicturePreviewActivity", "scaleFloat:$scaleFloat")
        binding.image.scale = scaleFloat
        binding.seekBar.progress = ((scaleFloat * 100 / 8).toInt())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            pictureResult = null
        }
    }

    private fun saveImage(bitmap: Bitmap): Uri? {
        val path = PathUtils.getFilesPathExternalFirst() + "/capture/" + System.currentTimeMillis().toString() + ".jpg"
        FileUtils.createOrExistsFile(path)
        val imageFile: File = FileUtils.getFileByPath(path)
        try {
            val fos = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Utils.insertImageToAlbum(this@PreviewActivity, imageFile)
    }

    private fun disableView(boolean: Boolean) {
        binding.btnAdd.isEnabled = boolean
        binding.btnReduce.isEnabled = boolean
    }

    private fun disableClick(boolean: Boolean) {
        binding.btnBack.isEnabled = boolean
        binding.btnDownload.isEnabled = boolean
        binding.seekBar.isEnabled = boolean
    }
}