package com.example.classtrack.data.api

import android.util.Base64
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class SignatureInterceptor(
    private val accessKeyId: String,
    private val accessKeySecret: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 确保是 FormBody
        val formBody = originalRequest.body as? FormBody
        val params = mutableMapOf<String, String>()

        // 将原始的请求参数加入到 map 中
        formBody?.let {
            for (i in 0 until it.size) {
                params[it.name(i)] = it.value(i)
            }
        }

        // 添加签名相关的系统参数
        params["AccessKeyId"] = accessKeyId
        params["Timestamp"] = getFormattedTimestamp()
        params["SignatureMethod"] = "HMAC-SHA1"
        params["SignatureVersion"] = "1.0"
        params["SignatureNonce"] = UUID.randomUUID().toString()

        // 添加API版本参数
        params["Version"] = "2016-08-01"  // 替换为阿里云推送API文档中的版本号

        // 生成签名
        val signature = generateSignature(params, accessKeySecret)
        params["Signature"] = signature

        // 构造新的 FormBody
        val newFormBody = FormBody.Builder().apply {
            params.forEach { (key, value) -> add(key, value) }
        }.build()

        // 构造新的请求
        val newRequest = originalRequest.newBuilder()
            .post(newFormBody)
            .build()

        return chain.proceed(newRequest)
    }

    // 获取当前的 ISO8601 格式时间戳
    private fun getFormattedTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(Date())
    }
}




fun generateSignature(params: Map<String, String>, accessKeySecret: String): String {
    val sortedParams = params.toSortedMap()  // 按字典顺序排序
    val queryString = sortedParams.entries.joinToString("&") {
        "${it.key}=${percentEncode(it.value)}"
    }

    val stringToSign = "POST&%2F&" + percentEncode(queryString)
    val keySpec = SecretKeySpec((accessKeySecret + "&").toByteArray(), "HmacSHA1")
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(keySpec)

    val rawHmac = mac.doFinal(stringToSign.toByteArray())
    return Base64.encodeToString(rawHmac, Base64.NO_WRAP)
}

fun percentEncode(value: String): String {
    return URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~")
}
