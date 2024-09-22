package com.example.classtrack.ui.screens

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.QueryListener
import com.example.classtrack.data.Class
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import javax.inject.Inject


@HiltViewModel
class AddStuScreenViewModel @Inject constructor() :ViewModel(){
    // 用于存储查询到的 Class 实体
    private val _class = MutableLiveData<com.example.classtrack.data.Class?>()
    val classLiveData: LiveData<com.example.classtrack.data.Class?> get() = _class

    // 根据 classId 获取对应的 Class 实体
    fun fetchClassById(classId: String) {
        val query = BmobQuery<com.example.classtrack.data.Class>()
        query.getObject(classId, object : QueryListener<com.example.classtrack.data.Class>() {
            override fun done(classObj: Class?, e: BmobException?) {
                if (e == null) {
                    // 成功获取到 Class 实体
                    _class.value = classObj
                } else {
                    // 处理错误
                    Log.e("YourViewModel", "Error fetching class: ${e.message}")
                }
            }
        })
    }

    fun generateClassQRCode(classId: String): Bitmap? {
        // 创建包含班级ID的JSON数据
        val jsonObject = JSONObject().apply {
            put("type", "join_class")  // 表示这是加课二维码
            put("classId", classId)    // 班级ID
        }
        // 将JSON转为字符串
        val jsonString = jsonObject.toString()

        try {
            // 使用ZXing生成二维码
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(jsonString, BarcodeFormat.QR_CODE, 300, 300)
            val barcodeEncoder = BarcodeEncoder()
            return barcodeEncoder.createBitmap(bitMatrix) // 返回二维码图像
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null // 如果生成失败，返回null
    }

    fun saveQRCodeToGallery(context: Context, bitmap: Bitmap): Uri? {
        val filename = "QRCode_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
            }
        }

        return uri
    }

    fun shareQRCode(context: Context, bitmap: Bitmap) {
        val uri = saveQRCodeToGallery(context, bitmap)
        uri?.let {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, it)
                type = "image/png"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share the QR code "))
        }
    }


}