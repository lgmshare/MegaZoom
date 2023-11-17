package com.zcba.megazoom.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.gsls.gt.GT
import com.zcba.megazoom.R
import com.zcba.megazoom.ui.HomeActivity

class NotificationUtils {

    companion object {

        // 添加常驻通知
        fun setNotification(context: Context) {
            val builder = GT.GT_Notification.createNotificationFoldView(
                context,
                R.mipmap.ic_launcher,//通知栏图标
                R.layout.notify_layout,//折叠布局
                R.layout.notify_layout,//展开布局
                false,//单击是否取消通知
                true,//是锁屏显示
                Intent(context, HomeActivity::class.java),//单击意图
                0,//发送通知时间
                1//通知Id
            );

            //启动最终的通知栏
            GT.GT_Notification.startNotification(builder, 1)
        }

        // 取消通知
        fun cancelNotification(context: Context) {
            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager!!.cancel(R.string.app_name)
        }

        /**
         * 创建服务通知
         */
        fun createForegroundNotification(context: Context): Notification {
            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?

            // 唯一的通知通道的id.
            val notificationChannelId = "notification_channel_id_01"

            // Android8.0以上的系统，新建消息通道
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //用户可见的通道名称
                val channelName = "MegaZoom Notification"
                //通道的重要程度
                val importance = NotificationManager.IMPORTANCE_HIGH
                val notificationChannel = NotificationChannel(notificationChannelId, channelName, importance)
                notificationChannel.setDescription("MegaZoom")
                //LED灯
                notificationChannel.enableLights(true)
                notificationChannel.setLightColor(Color.RED)
                //震动
                notificationChannel.setVibrationPattern(longArrayOf(0, 1000, 500, 1000))
                notificationChannel.enableVibration(true)
                notificationManager?.createNotificationChannel(notificationChannel)
            }
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, notificationChannelId)
            //通知小图标
            builder.setSmallIcon(R.mipmap.ic_launcher)
            //设定通知显示的时间
            builder.setWhen(System.currentTimeMillis())
            val layoutView1: RemoteViews = RemoteViews(context.packageName, R.layout.notify_layout)
            val layoutView2: RemoteViews = RemoteViews(context.packageName, R.layout.notify_layout)
            builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            builder.setCustomBigContentView(layoutView2)
            builder.setCustomContentView(layoutView1)
            //设定启动的内容
            val activityIntent = Intent(context, HomeActivity::class.java)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 1, activityIntent, PendingIntent.FLAG_IMMUTABLE)
            builder.setContentIntent(pendingIntent)

            //创建通知并返回
            return builder.build()
        }
    }
}