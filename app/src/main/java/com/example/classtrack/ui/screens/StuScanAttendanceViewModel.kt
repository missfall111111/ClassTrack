package com.example.classtrack.ui.screens

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import com.example.classtrack.data.Attendance
import com.example.classtrack.data.StudentCheckIn
import com.example.classtrack.ui.util.LocationHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class StuScanAttendanceViewModel @Inject constructor():ViewModel() {

    fun handleCheckInScanResult(scanResult: String, context: Context) {
        val locationHelper = LocationHelper(context)

        try {
            val jsonObject = JSONObject(scanResult)
            val type = jsonObject.getString("type")
            val classId = jsonObject.getString("classId")
            val timestamp = jsonObject.getLong("timestamp")

            if (type != "attendance") {
                Log.e("StuCheckIn", "不是有效的签到二维码")
                return
            }

            val currentTime = System.currentTimeMillis()
            val validDuration = 10*1000 // 10s
            if (currentTime - timestamp > validDuration) {
                Log.e("StuCheckIn", "二维码已过期")
                return
            }

            val studentId = getCurrentStudentId()

            locationHelper.getCurrentLatitude { latitude ->
                locationHelper.getCurrentLongitude { longitude ->
                    val checkInTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                    val studentCheckIn = StudentCheckIn(
                        studentId = studentId,
                        checkInTime = checkInTime,
                        latitude = latitude,
                        longitude = longitude
                    )

                    Log.d("StuCheckIn","$studentCheckIn")
                    updateAttendanceData(classId, studentCheckIn)
                }
            }

        } catch (e: JSONException) {
            Log.e("StuCheckIn", "二维码内容解析失败: ${e.message}")
        }
    }


    private fun getCurrentStudentId(): String {
        // 获取当前学生的ID（从登录信息或用户数据中获取）
        val currentUser: BmobUser? = BmobUser.getCurrentUser(BmobUser::class.java)
        return currentUser?.objectId ?: ""

    }

    private fun updateAttendanceData(classId: String, studentCheckIn: StudentCheckIn) {
        // 查询该班级的签到数据，并更新学生的签到信息
        val query = BmobQuery<Attendance>()
        query.addWhereEqualTo("classId", classId)
        query.findObjects(object : FindListener<Attendance>() {
            override fun done(attendanceList: List<Attendance>?, e: BmobException?) {
                if (e == null) {
                    if (!attendanceList.isNullOrEmpty()) {
//                        val attendance :Attendance= attendanceList.last()
                        val oldAttendance=attendanceList.last()
                        val currentStudent=oldAttendance.presentStudents
                        Log.d("StuCheckIn", "Attendance List size: ${attendanceList.size}") // 输出 objectId
//                        Log.d("StuCheckIn", "Attendance info: ${attendance}") // 输出 objectId
//                        Log.d("StuCheckIn", "Attendance objectId: ${attendance.objectId}") // 输出 objectId
//                        attendance.presentStudents.add(studentCheckIn)
//                        attendance.add("presentStudents",studentCheckIn)
                        if(!currentStudent.contains(studentCheckIn)){
                            currentStudent.add(studentCheckIn)
                        }
                        else{
                            Log.d("StuCheckIn", "You are already attendance")
                            return
                        }

                        val updatedAttendance=Attendance(oldAttendance.classId,
                            checkInType = oldAttendance.checkInType,
                            passcode = oldAttendance.passcode,
                            time = oldAttendance.time,
                            presentStudents = currentStudent,
                            isEnded = oldAttendance.isEnded)
                        updatedAttendance.objectId=oldAttendance.objectId
//                        attendance.addUnique("presentStudents", studentCheckIn)
                        updatedAttendance.update(object : UpdateListener() {
                            override fun done(e: BmobException?) {
                                if (e == null) {
                                    Log.d("StuCheckIn", "签到成功")
                                } else {
                                    Log.e("StuCheckIn", "签到更新失败: ${e.message}")
                                }
                            }
                        })
                    } else {
                        Log.e("StuCheckIn", "找不到该班级的签到数据")
                    }
                } else {
                    Log.e("StuCheckIn", "查询失败: ${e.message}")
                }
            }
        })
    }
}