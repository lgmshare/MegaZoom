package com.zcba.megazoom.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.style.BottomNavBarStyle
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.style.SelectMainStyle
import com.luck.picture.lib.style.TitleBarStyle
import com.zcba.megazoom.R
import com.zcba.megazoom.app.BaseActivity
import com.zcba.megazoom.databinding.HomeActivityBinding
import com.zcba.megazoom.service.ForegroundService
import com.zcba.megazoom.utils.GlideEngine
import com.zcba.megazoom.utils.Utils


class HomeActivity : BaseActivity<HomeActivityBinding>() {

    override fun onBuildBinding(): HomeActivityBinding {
        return HomeActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.tvHome.text = "Expand your outlook, uplift your surroundings, and emanate with wisdom"

        binding.run {
            btnMagnifier.setOnClickListener {
                pickCamera()
            }

            btnPhoto.setOnClickListener {
                pickImage()
            }
        }

        //启动服务
        if (!ForegroundService.serviceIsLive) {
            // Android 8.0使用startForegroundService在前台启动新服务
//            val intent = Intent(this, ForegroundService::class.java)
//            intent.putExtra("Foreground", "This is a foreground service.")
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                startForegroundService(intent)
//            } else {
//                startService(intent)
//            }
        } else {
            Utils.log("前台服务正在运行中...")
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun pickCamera() {
        //startActivity(Intent(this@HomeActivity, GoogleCameraActivity::class.java))
        startActivity(Intent(this@HomeActivity, CameraActivity::class.java))
    }

    private fun pickImage() {
        val whiteTitleBarStyle = TitleBarStyle()
        whiteTitleBarStyle.titleBackgroundColor = ContextCompat.getColor(this@HomeActivity, R.color.ps_color_white)
        whiteTitleBarStyle.titleDrawableRightResource = R.drawable.ic_arrow_down_24
        whiteTitleBarStyle.titleLeftBackResource = R.mipmap.ps_ic_black_back
        whiteTitleBarStyle.titleTextColor = ContextCompat.getColor(this@HomeActivity, R.color.ps_color_black)
        whiteTitleBarStyle.titleCancelTextColor = ContextCompat.getColor(this@HomeActivity, R.color.ps_color_53575e)
        whiteTitleBarStyle.isDisplayTitleBarLine = true

        val whiteBottomNavBarStyle = BottomNavBarStyle()
        whiteBottomNavBarStyle.bottomNarBarBackgroundColor = ContextCompat.getColor(this@HomeActivity, R.color.ps_color_white)
        whiteBottomNavBarStyle.bottomPreviewSelectTextColor = ContextCompat.getColor(this@HomeActivity, R.color.ps_color_53575e)

        whiteBottomNavBarStyle.bottomPreviewNormalTextColor = ContextCompat.getColor(this@HomeActivity, R.color.ps_color_9b)
        whiteBottomNavBarStyle.bottomPreviewSelectTextColor = ContextCompat.getColor(this@HomeActivity, R.color.black)
        whiteBottomNavBarStyle.isCompleteCountTips = false
        whiteBottomNavBarStyle.bottomEditorTextColor = ContextCompat.getColor(this@HomeActivity, R.color.ps_color_53575e)
        whiteBottomNavBarStyle.bottomOriginalTextColor = ContextCompat.getColor(this@HomeActivity, R.color.ps_color_53575e)

        val selectMainStyle = SelectMainStyle()
        selectMainStyle.statusBarColor = ContextCompat.getColor(this@HomeActivity, R.color.ps_color_white)
        selectMainStyle.isDarkStatusBarBlack = true
        selectMainStyle.selectNormalTextColor = ContextCompat.getColor(this@HomeActivity, R.color.ps_color_9b)
        selectMainStyle.selectTextColor = ContextCompat.getColor(this@HomeActivity, R.color.black)
        selectMainStyle.previewSelectBackground = R.drawable.ps_checkbox_selector
        selectMainStyle.selectBackground = R.drawable.ps_checkbox_selector
        selectMainStyle.setSelectText(R.string.ps_done_front_num)
        selectMainStyle.mainListBackgroundColor = ContextCompat.getColor(this@HomeActivity, R.color.ps_color_white)

        val selectorStyle = PictureSelectorStyle()
        selectorStyle.setTitleBarStyle(whiteTitleBarStyle)
        selectorStyle.setBottomBarStyle(whiteBottomNavBarStyle)
        selectorStyle.setSelectMainStyle(selectMainStyle)

        PictureSelector.create(this@HomeActivity)
            .openGallery(SelectMimeType.ofImage())
            .isQuickCapture(false)
            .isAutoVideoPlay(false)
            .isDisplayCamera(false)
            .isPreviewImage(false)
            .isPreviewFullScreenMode(false)
            .isPreviewZoomEffect(false)
            .setMaxSelectNum(1)
            .setSelectorUIStyle(selectorStyle)
            .setImageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>) {
                    val localMedia = result[0]
                    val path: String = localMedia.availablePath
                    startActivity(Intent(this@HomeActivity, FocusActivity::class.java).apply {
                        putExtra("path", path)
                    })
                }

                override fun onCancel() {

                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        //停止服务
        //val intent = Intent(this, ForegroundService::class.java)
        //stopService(intent)
    }
}