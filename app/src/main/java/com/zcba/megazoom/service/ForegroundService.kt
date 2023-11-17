package com.zcba.megazoom.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Nullable
import com.zcba.megazoom.utils.NotificationUtils
import com.zcba.megazoom.utils.Utils


class ForegroundService : Service() {

    companion object {

        const val NOTIFICATION_ID = 360
        var serviceIsLive = false
    }

    override fun onCreate() {
        super.onCreate()
        Utils.log("onCreate")
        val notification = NotificationUtils.createForegroundNotification(this)
        startForeground(NOTIFICATION_ID, notification)
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        Utils.log("onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Utils.log("onStartCommand")
        // 标记服务启动
        ForegroundService.serviceIsLive = true
        // 数据获取
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Utils.log("onDestroy")
        ForegroundService.serviceIsLive = false
        stopForeground(true)
        super.onDestroy()
    }

}