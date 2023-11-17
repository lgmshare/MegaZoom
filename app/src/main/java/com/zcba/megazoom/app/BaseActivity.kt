package com.zcba.megazoom.app

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.gsls.gt.GT
import com.zcba.megazoom.utils.NotificationUtils

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = onBuildBinding()
        setContentView(binding.root)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    abstract fun onBuildBinding(): VB

    fun postNotification() {
        //通知栏权限申请
        GT.AppAuthorityManagement.Permission.init(this, Manifest.permission.POST_NOTIFICATIONS)
            .permissions(object : GT.AppAuthorityManagement.Permission.OnPermissionListener {
                override fun onExplainRequestReason(onPDListener: GT.AppAuthorityManagement.Permission.PermissionDescription) {
                    onPDListener.isAcceptAdvice = true //核心，设置拒绝授权
                }

                override fun onForwardToSettings(): Boolean {
                    //特殊权限特殊处理，如：需要进入 系统设置 中或 应用信息中的代码可自定义填写
                    return true //默认是false 一定有改过来设置为 true
                }

                override fun request(allGranted: Boolean, grantedList: Array<String>, deniedList: Array<String>, message: String) {
                    if (allGranted) {
                        //通知栏权限授予，进行发布通知栏操作
                        NotificationUtils.setNotification(applicationContext)
                    } else {
                        //未全部授权,通知用户
                    }
                }
            })
    }

}