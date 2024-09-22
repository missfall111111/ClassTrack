package com.example.classtrack.ui.util

import com.aliyun.auth.credentials.Credential
import com.aliyun.auth.credentials.provider.StaticCredentialProvider
import com.aliyun.sdk.service.push20160801.models.PushNoticeToAndroidRequest
import com.aliyun.sdk.service.push20160801.models.PushNoticeToAndroidResponse
import com.aliyun.sdk.service.push20160801.AsyncClient
import com.google.gson.Gson
import darabonba.core.client.ClientOverrideConfiguration
import java.util.concurrent.CompletableFuture

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext

// 定义一个函数用于发布推送通知
suspend fun sendPushNotificationToAndroid(
    appKey: Long,
    accessKeyId: String,
    accessKeySecret: String,
    deviceIds: List<String>, // 接受多个设备ID
    title: String,
    body: String,
) {
    withContext(Dispatchers.IO) {
        try {
            // 设置凭据
            val provider = StaticCredentialProvider.create(
                Credential.builder()
                    .accessKeyId(accessKeyId)
                    .accessKeySecret(accessKeySecret)
                    .build()
            )

            // 初始化客户端
            val client = AsyncClient.builder()
                .region("cn-hangzhou") // 设置地域
                .credentialsProvider(provider)
                .overrideConfiguration(
                    ClientOverrideConfiguration.create()
                        .setEndpointOverride("cloudpush.aliyuncs.com")
                )
                .build()

            // 将多个设备ID拼接成逗号分隔的字符串
            val targetValue = deviceIds.joinToString(",")

            // 构建请求参数
            val pushNoticeToAndroidRequest = PushNoticeToAndroidRequest.builder()
                .appKey(appKey)
                .target("DEVICE") // 推送目标是设备
                .targetValue(targetValue) // 多个设备ID
                .title(title) // 通知标题
                .body(body) // 通知内容
                .build()

            // 异步发送推送请求并等待结果
            val response = client.pushNoticeToAndroid(pushNoticeToAndroidRequest).await()

            println("推送成功: ${Gson().toJson(response)}")

            // 关闭客户端
            client.close()

        } catch (e: Exception) {
            println("推送失败: ${e.message}")
        }
    }
}


// 调用示例
//fun main() {
//    val appKey: Long = 334958991L // 替换为你的appKey
//    val accessKeyId = "your-access-key-id" // 替换为你的accessKeyId
//    val accessKeySecret = "your-access-key-secret" // 替换为你的accessKeySecret
//    val deviceIds = listOf("85c66b43ffa749d7a43c0170804b9a33", "1bbdc4a901404565a3078cd7caa84ec7") // 替换为实际的设备ID列表
//    val title = "签到通知"
//    val body = "教师已发布新的签到，请及时签到"
//
//    sendPushNotificationToAndroid(appKey, accessKeyId, accessKeySecret, deviceIds, title, body)
//}



