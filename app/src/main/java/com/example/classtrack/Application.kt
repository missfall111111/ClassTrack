package com.example.classtrack

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobInstallation
import cn.bmob.v3.BmobInstallationManager
import cn.bmob.v3.InstallationListener
import cn.bmob.v3.exception.BmobException
import com.alibaba.sdk.android.push.CloudPushService
import com.alibaba.sdk.android.push.noonesdk.PushInitConfig
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ClassTrackApp:Application() {
    override fun onCreate() {
        super.onCreate()
        Bmob.initialize(this,"66733bc151b1caa55bbc3bff0e64266b")




        //创建渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val group = NotificationChannelGroup("aliyunGroup", "aliyunChannelGroup")
            notificationManager.createNotificationChannelGroup(group)

            val channel = NotificationChannel(
                "aliyun_push_demo",
                "aliyunChannel",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Aliyun Notification Description"
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(null,null)
            channel.group = "aliyunGroup"
            notificationManager.createNotificationChannel(channel)
        }


        val pushInitConfig = PushInitConfig.Builder()
            .application(this)
            .appKey("334958991")    //请填写你自己的appKey
            .appSecret("495c886c050e4b0681915aeaefa861f8")    //请填写你自己的appSecret
            .build()


        PushServiceFactory.init(pushInitConfig)


        val pushService = PushServiceFactory.getCloudPushService()
        pushService.register(this, object : com.alibaba.sdk.android.push.CommonCallback {
            override fun onSuccess(success: String) {
                Log.d("PushService", "阿里云推送注册成功")
            }
            override fun onFailed(errorCode: String, errorMessage: String) {
                Log.e("PushService", "推送注册失败，错误码: $errorCode 错误信息: $errorMessage")
            }
        })
    }
}

