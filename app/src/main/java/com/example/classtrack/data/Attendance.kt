package com.example.classtrack.data

import androidx.lifecycle.MutableLiveData
import cn.bmob.v3.BmobObject
import java.io.Serializable

data class StudentCheckIn(
    val studentId: String,  // 学生ID
    val checkInTime: String,  // 签到时间
    val latitude: Double?,    // 签到时的纬度
    val longitude: Double?    // 签到时的经度
)

data class Attendance(
    val classId: String,          // 班级ID
    val time: String,             // 签到创建时间
    val checkInType: String,      // 签到类型：QRCode 或 Passcode
    val passcode: String? = null, // 如果是口令签到，则需要存储口令
    val presentStudents: MutableList<StudentCheckIn> = mutableListOf() ,// 出席学生的签到信息
    var isEnded: Boolean = false  // 标记签到是否结束
) : BmobObject()
