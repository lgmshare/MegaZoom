package com.zcba.megazoom.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.lifecycleScope
import com.zcba.megazoom.app.App
import com.zcba.megazoom.app.BaseActivity
import com.zcba.megazoom.app.CloakHelper
import com.zcba.megazoom.databinding.WelcomeActivityBinding
import com.zcba.megazoom.utils.NotificationUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class WelcomeActivity : BaseActivity<WelcomeActivityBinding>() {
    private var job: Job? = null

    override fun onBuildBinding(): WelcomeActivityBinding {
        return WelcomeActivityBinding.inflate(layoutInflater)
    }

    override fun onStart() {
        super.onStart()
        CloakHelper.request()

        binding.progressBar.progress = 0

        if (App.INSTANCE.activityCount > 1) {
            job = lifecycleScope.launch {
                kotlin.runCatching {
                    withTimeoutOrNull(5000) {
                        launch {
                            var progress = binding.progressBar.progress
                            while (isActive && progress <= 100) {
                                progress++
                                binding.progressBar.progress = progress
                                delay(30)
                            }
                        }
                    }
                }.onSuccess {
                    load()
                }
            }
        } else {
            job = lifecycleScope.launch {
                kotlin.runCatching {
                    withTimeoutOrNull(5000) {
                        launch {
                            var progress = binding.progressBar.progress
                            while (isActive && progress <= 100) {
                                progress++
                                binding.progressBar.progress = progress
                                delay(40)
                            }
                        }
                    }
                }.onSuccess {
                    load()
                }
            }
        }


    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
    }

    override fun onBackPressed() {
    }

    private fun load() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            goAuth()
            return
        }

        if (Build.VERSION.SDK_INT < 33) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                goAuth()
                return
            }
        }
        goHome()
    }

    private fun goHome() {
        startActivity(Intent(this@WelcomeActivity, HomeActivity::class.java))
        finish()
    }

    private fun goAuth() {
        startActivity(Intent(this@WelcomeActivity, AuthActivity::class.java))
        finish()
    }
}