package com.zcba.megazoom.ui

import android.Manifest
import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.PermissionUtils
import com.gsls.gt.GT
import com.gsls.gt.GT.AppAuthorityManagement.Permission
import com.gsls.gt.GT.AppAuthorityManagement.Permission.OnPermissionListener
import com.gsls.gt.GT.AppAuthorityManagement.Permission.PermissionDescription
import com.gsls.gt.GT.GT_Dialog.GT_AlertDialog
import com.zcba.megazoom.R
import com.zcba.megazoom.app.BaseActivity
import com.zcba.megazoom.databinding.AuthActivityBinding
import com.zcba.megazoom.databinding.DialogSimpleBinding


class AuthActivity : BaseActivity<AuthActivityBinding>() {

    private var dialog: Dialog? = null

    override fun onBuildBinding(): AuthActivityBinding {
        return AuthActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.tvAuth.text = "To guarantee the correct operation of this feature, kindly grant the required file access and camera permissions"

        binding.btnAgree.setOnClickListener {
            requestPermission()
        }
        binding.btnCancel.setOnClickListener {
            ActivityUtils.finishAllActivities()
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            PermissionUtils.permission(Manifest.permission.CAMERA).callback(object :
                PermissionUtils.FullCallback {
                override fun onGranted(granted: MutableList<String>) {
                    goHome()
                }

                override fun onDenied(deniedForever: MutableList<String>, denied: MutableList<String>) {
                    if (deniedForever.isNotEmpty()) {
                        showTips()
                    }
                }
            }).request()
        } else {
            PermissionUtils.permission(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE).callback(object :
                PermissionUtils.FullCallback {
                override fun onGranted(granted: MutableList<String>) {
                    goHome()
                }

                override fun onDenied(deniedForever: MutableList<String>, denied: MutableList<String>) {
                    if (deniedForever.isNotEmpty()) {
                        showTips()
                    }
                }
            }).request()
        }
    }

    private fun goHome() {
        startActivity(Intent(this@AuthActivity, HomeActivity::class.java))
        finish()
    }

    private fun showTips() {
        showTipsDialog() {
            start()
        }
    }

    private fun showTipsDialog(confirmCallback: (() -> Unit)) {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
        val rootBinding = DialogSimpleBinding.inflate(layoutInflater)
        dialog = Dialog(this).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setContentView(rootBinding.root)
        }

        rootBinding.tvContent.text = "Please enable permissions in application settings"

        rootBinding.btnConfirm.setOnClickListener {
            dialog?.dismiss()
            confirmCallback.invoke()
        }
        rootBinding.btnCancel.setOnClickListener {
            dialog?.dismiss()
        }
        dialog?.window?.also {
            it.attributes?.also { attr ->
                attr.width = ActionBar.LayoutParams.MATCH_PARENT
                attr.height = ActionBar.LayoutParams.WRAP_CONTENT
                it.attributes = attr
            }
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setGravity(Gravity.BOTTOM)
        }
        dialog?.show()
    }

    override fun onPause() {
        super.onPause()
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
        dialog = null
    }

    override fun onBackPressed() {
    }

    //跳转
    private fun start() {
        PermissionUtils.launchAppDetailsSettings()
    }
}