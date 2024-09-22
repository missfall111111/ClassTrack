package com.example.classtrack.ui.screens

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import com.example.classtrack.data.Attendance
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject


@HiltViewModel
class TeaQRViewModel @Inject constructor():ViewModel(){

    private val _qrCodeBitmap = MutableLiveData<Bitmap?>()
    val qrCodeBitmap: LiveData<Bitmap?> = _qrCodeBitmap

    private var timerJob: Job? = null

    fun startGeneratingAttendanceQrCode(classId: String) {
        // 开始一个协程任务，每10秒生成一次新的二维码
        timerJob = viewModelScope.launch {
            while (isActive) {
                // 生成新的二维码
                val qrCode = generateQRCodeContent(classId)
                _qrCodeBitmap.postValue(qrCode)
                delay(10000L)  // 每10秒生成一次新的二维码
            }
        }
    }

    fun stopGeneratingQrCode() {
        // 停止生成二维码的任务
        timerJob?.cancel()
    }

    // 生成二维码的实际逻辑
    private fun generateQRCodeContent(classId: String): Bitmap? {

        // 创建JSON对象并填充数据
        val jsonObject = JSONObject().apply {
            put("type", "attendance")  // 表示这是签到二维码
            put("classId", classId)    // 班级ID
            put("timestamp", System.currentTimeMillis()) // 当前时间戳，用于检查二维码是否过期
        }

        // 将JSON对象转换为字符串
        val content = jsonObject.toString()

        // 根据 content 生成二维码
        return generateQRCode(content)
    }

    private fun generateQRCode(content: String): Bitmap? {
        // 使用 ZXing 生成二维码
        try {
            val bitMatrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 300, 300)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            return bmp
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }
    }

    // 查询并更新最后一个签到状态为已结束
    fun endAttendanceByClassId(classId: String) {
        val query = BmobQuery<Attendance>()
        query.addWhereEqualTo("classId", classId)
        query.findObjects(object : FindListener<Attendance>() {
            override fun done(attendanceList: List<Attendance>?, e: BmobException?) {
                if (e == null && !attendanceList.isNullOrEmpty()) {
                    // 获取最后一个签到记录
                    val oldAttendance = attendanceList.last()

                    if(oldAttendance.isEnded==false){
                        val updatedAttendance=Attendance(classId=oldAttendance.classId,
                            checkInType = oldAttendance.checkInType,
                            passcode = oldAttendance.passcode,
                            presentStudents = oldAttendance.presentStudents,
                            time = oldAttendance.time,
                            isEnded = true)
                        updatedAttendance.objectId=oldAttendance.objectId

                        // 更新最后一个签到的状态
                        updatedAttendance.update(object : UpdateListener() {
                            override fun done(e: BmobException?) {
                                if (e == null) {
                                    // 更新成功
                                    Log.d("Attendance", "签到状态已更新为已结束")
                                } else {
                                    // 更新失败
                                    Log.e("Attendance", "更新签到状态失败: ${e.message}")
                                }
                            }
                        })
                    }
                    else{
                        return
                    }
                } else {
                    // 查询失败或未找到签到记录
                    Log.e("Attendance", "查询失败或无记录: ${e?.message}")
                }
            }
        })
    }
}