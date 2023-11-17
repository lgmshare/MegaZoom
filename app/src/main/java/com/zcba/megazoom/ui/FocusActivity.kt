package com.zcba.megazoom.ui

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.widget.SeekBar
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.luck.picture.lib.config.PictureMimeType
import com.zcba.megazoom.app.BaseActivity
import com.zcba.megazoom.view.MagnifierView
import com.zcba.megazoom.databinding.FocusActivityBinding


class FocusActivity : BaseActivity<FocusActivityBinding>() {

    private var mv: MagnifierView? = null

    private var scale: Float = 0.0f

    override fun onBuildBinding(): FocusActivityBinding {
        return FocusActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val path = intent.getStringExtra("path")
        Glide.with(this@FocusActivity)
            .load(if (PictureMimeType.isContent(path)) Uri.parse(path) else path)
            .centerInside()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean,
                ): Boolean {
                    binding.coverImg.setImageDrawable(resource)
                    reset(1.0f)
                    return true
                }
            })
            .into(binding.coverImg)

        binding.root.postDelayed({

        }, 300)

        binding.btnBack.setOnClickListener {
            finish()
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
        reset(scaleFloat)
    }

    private fun toggleZoom(add: Boolean) {
        var scaleFloat = this.scale
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
        binding.seekBar.progress = ((scaleFloat * 100 / 8).toInt())
        reset(scaleFloat)
    }

    private fun reset(scale: Float) {
        this.scale = scale
        if (mv == null) {
            val rctWidth = ScreenUtils.getScreenWidth()
            val height = ScreenUtils.getScreenHeight()
            val rctHeight = ScreenUtils.getScreenHeight() / 3
            mv = MagnifierView.Builder(this@FocusActivity)
                .intiLT(rctWidth / 2 - rctHeight / 2, height / 2 - rctHeight / 2)
                .viewWH(rctHeight, rctHeight)
                .scale(scale)
                .alpha(32)
                .rootVg(binding.touchLayout)
                .color("#ffffff")
                .build()
            //打开放大镜
            mv?.startViewToRoot()
        } else {
            mv?.setScaleZoom(scale)
            mv?.invalidate()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mv != null) {
            //关闭放大镜
            mv?.closeViewToRoot()
        }
    }

    private var downX: Float = 0f
    private var downY: Float = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.getAction()) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.getRawX()
                downY = event.getRawY()
            }

            MotionEvent.ACTION_UP -> {
                val upX = event.getRawX()
                val upY = event.getRawY()
                if (Math.abs(upX - downX) < 10 && Math.abs(upY - downY) < 10) {
                    mv?.setXY(upX, upY)
                }
            }
        }
        return super.onTouchEvent(event)
    }
}