package com.example.classtrack

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.alibaba.sdk.android.push.MessageReceiver
import com.alibaba.sdk.android.push.notification.CPushMessage


class MyMessageReceiver: MessageReceiver(){


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNotificationReceivedInApp(
        context: Context?,
        title: String?,
        summary: String?,
        map: MutableMap<String, String>?,
        openType: Int,
        openActivity: String?,
        openUrl: String?
    ) {
        Log.d("MyMessageReceiver", "onNotificationReceivedInApp triggered")
        //这里可以处理下发的推送通知
        context?.let {
            Log.d("MyMessageReceiver", "Context is not null")
            // 获取 NotificationManager
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            Log.d("MyMessageReceiver", "NotificationManager retrieved")

            // 设置与 Application 中一致的 channelId
            val channelId = "aliyun_push_demo"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = notificationManager.getNotificationChannel(channelId)
                if (channel == null) {
                    Log.d("MyMessageReceiver", "NotificationChannel not found!")
                    return
                }
                else
                {
                    Log.d("MyMessageReceiver", "NotificationChannel  found!$channel")
                }
            }

            // 创建通知
            val notificationBuilder = Notification.Builder(context, channelId)
                .setContentTitle(title ?: "推送标题")
                .setContentText(summary ?: "推送内容")
                .setSmallIcon(R.drawable.ic_launcher_foreground)  // 替换为你的图标
                .setAutoCancel(true)

            // 发送通知
            notificationManager.notify(1001, notificationBuilder.build())
        }
    }

    override fun onNotificationOpened(p0: Context?, p1: String?, p2: String?, p3: String?) {
        TODO("Not yet implemented")
    }

    override fun onNotificationRemoved(p0: Context?, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun showNotificationNow(p0: Context?, p1: MutableMap<String, String>?): Boolean {
        //false表示拦截，true表示不拦截，请根据进行拦截，拦截后会执行到 onNotificationReceivedInApp
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNotification(
        context: Context?,
        p1: String?,
        p2: String?,
        p3: MutableMap<String, String>?
    ) {
//        TODO("Not yet implemented")
        Log.d("onNotification", "Received push notification: $p1 - $p2")
//        Log.d("onNotification", "onNotification: $p0")
//        Log.d("onNotification", "onNotification: $p1")
//        Log.d("onNotification", "onNotification: $p2")
//        Log.d("onNotification", "onNotification: $p3")

//        context?.let {
//            Log.d("MyMessageReceiver", "Context is not null")
//            // 获取 NotificationManager
//            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//            Log.d("MyMessageReceiver", "NotificationManager retrieved")
//
//            // 设置与 Application 中一致的 channelId
//            val channelId = "aliyun_push_demo"
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channel = notificationManager.getNotificationChannel(channelId)
//                if (channel == null) {
//                    Log.d("MyMessageReceiver", "NotificationChannel not found!")
//                    return
//                }
//                else
//                {
//                    Log.d("MyMessageReceiver", "NotificationChannel  found!$channel")
//                }
//            }
//
//
//            // 创建通知
//            val notificationBuilder = Notification.Builder(context, channelId)
//                .setContentTitle(p1 ?: "推送标题")
//                .setContentText(p2 ?: "推送内容")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)  // 替换为你的图标
//                .setAutoCancel(true)
//
//            // 发送通知
//            notificationManager.notify(1001, notificationBuilder.build())
//        }



    }

    override fun onMessage(p0: Context?, p1: CPushMessage?) {
        TODO("Not yet implemented")
    }

    override fun onNotificationClickedWithNoAction(
        p0: Context?,
        p1: String?,
        p2: String?,
        p3: String?
    ) {
        TODO("Not yet implemented")
    }
}