package com.example.classtrack.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.QueryListener
import cn.bmob.v3.listener.UpdateListener
import com.example.classtrack.data.Attendance
import com.example.classtrack.data.Class
import com.example.classtrack.data.StudentCheckIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TeaAttendanceDetailViewModel @Inject constructor() : ViewModel() {
    private val _attendanceDetails = MutableStateFlow<List<StudentCheckIn>>(emptyList())
    val attendanceDetails: StateFlow<List<StudentCheckIn>> get() = _attendanceDetails


    private val _studentNameMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val studentNameMap: StateFlow<Map<String, String>> get() = _studentNameMap

    private val _isStudentNameMapLoaded = MutableStateFlow(false)
    val isStudentNameMapLoaded: StateFlow<Boolean> get() = _isStudentNameMapLoaded


    // 用于加载班级的学生及其签到状态
    fun loadAttendanceDetails(classId: String, attendanceId: String) {
        Log.d("Attendance","Start")
        val classQuery = BmobQuery<Class>()
        classQuery.getObject(classId, object : QueryListener<Class>() {
            override fun done(classObject: Class?, e: BmobException?) {
                if (e != null || classObject == null) {
                    Log.e("AttendanceDetails", "班级查询失败: ${e?.message}")
                    return
                }

                val allStudents = classObject.students

                Log.d("Attendance","Query all students ${allStudents.size}")
                // 查询签到记录
                val attendanceQuery = BmobQuery<Attendance>()
                attendanceQuery.getObject(attendanceId, object : QueryListener<Attendance>() {
                    override fun done(attendance: Attendance?, e: BmobException?) {
                        if (e != null || attendance == null) {
                            Log.e("AttendanceDetails", "签到记录查询失败: ${e?.message}")
                            return
                        }

                        Log.d("Attendance","Query attendance${attendance.objectId}")
                        // 先创建一个 Map 用来存储 studentId 和用户名的映射
                        val studentNameMap = mutableMapOf<String, String>()
                        var loadedCount = 0

                        allStudents.forEach { studentId ->
                            val userQuery = BmobQuery<BmobUser>()
                            userQuery.getObject(studentId, object : QueryListener<BmobUser>() {
                                override fun done(user: BmobUser?, e: BmobException?) {
                                    if (e == null && user != null) {
                                        // 将 studentId 映射到对应的用户名
                                        studentNameMap[studentId] = user.username ?: "未知用户"
                                    } else {
                                        Log.e("AttendanceDetails", "无法获取学生信息: ${e?.message}")
                                    }

                                    // 计数查询是否完成
                                    loadedCount++
                                    if (loadedCount == allStudents.size) {
                                        Log.d("Attendance","Hello1")
                                        _studentNameMap.value = studentNameMap // 设定学生名字映射
                                        _isStudentNameMapLoaded.value = true
                                    }
                                    else{
                                        Log.d("Attendance","Hello")
                                    }
                                }
                            })
                        }

                        // 生成学生签到状态列表
                        val studentStatusList = allStudents.map { studentId ->
                            val studentCheckIn = attendance.presentStudents.find { it.studentId == studentId }
                            studentCheckIn ?: StudentCheckIn(
                                studentId = studentId,
                                checkInTime = "未签到",
                                latitude = null,
                                longitude = null
                            )
                        }

                        if (studentStatusList.isEmpty()){
                            _isStudentNameMapLoaded.value = true
                        }
                        _attendanceDetails.value = studentStatusList
                    }
                })
            }
        })
    }
    fun getCurrentFormattedTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = Date(System.currentTimeMillis())
        return sdf.format(date)
    }

    // 学生签到 (Present)
    fun markStudentAsPresent(studentCheckIn: StudentCheckIn, attendanceId: String) {
        val attendanceQuery = BmobQuery<Attendance>()
        attendanceQuery.getObject(attendanceId, object : QueryListener<Attendance>() {
            override fun done(attendance: Attendance?, e: BmobException?) {
                if (e == null && attendance != null) {
                    val updatedCheckIn = studentCheckIn.copy(
                        checkInTime = getCurrentFormattedTime(), // 标记当前时间
                    )

                    val newAttendance=Attendance(classId = attendance.classId,
                        checkInType = attendance.checkInType,
                        passcode = attendance.passcode,
                        presentStudents = attendance.presentStudents,
                        time = attendance.time,
                        isEnded = attendance.isEnded)
                    newAttendance.presentStudents.removeIf { it.studentId == studentCheckIn.studentId }
                    newAttendance.presentStudents.add(updatedCheckIn)

                    newAttendance.objectId=attendance.objectId

                    newAttendance.update(attendance.objectId, object : UpdateListener() {
                        override fun done(e: BmobException?) {
                            if (e == null) {
                                Log.d("Attendance", "学生标记为签到成功")
                                // 更新本地UI
                                loadAttendanceDetails(attendance.classId, attendance.objectId)
                            } else {
                                Log.e("Attendance", "更新签到失败: ${e.message}")
                            }
                        }
                    })
                } else {
                    Log.e("Attendance", "查询Attendance失败: ${e?.message}")
                }
            }
        })
    }

    // 学生缺勤 (Absent)
    fun markStudentAsAbsent(studentCheckIn: StudentCheckIn, attendanceId: String) {
        val attendanceQuery = BmobQuery<Attendance>()
        attendanceQuery.getObject(attendanceId, object : QueryListener<Attendance>() {
            override fun done(attendance: Attendance?, e: BmobException?) {
                if (e == null && attendance != null) {
                    val absentCheckIn = studentCheckIn.copy(
                        checkInTime = "未签到"
                    )
                    val newAttendance=Attendance(classId = attendance.classId,
                        checkInType = attendance.checkInType,
                        passcode = attendance.passcode,
                        presentStudents = attendance.presentStudents,
                        time = attendance.time,
                        isEnded = attendance.isEnded)
                    newAttendance.presentStudents.removeIf { it.studentId == studentCheckIn.studentId }
                    newAttendance.presentStudents.add(absentCheckIn)

                    newAttendance.objectId=attendance.objectId
                    newAttendance.update(object : UpdateListener() {
                        override fun done(e: BmobException?) {
                            if (e == null) {
                                Log.d("Attendance", "学生标记为缺勤成功")
                                // 更新本地UI
                                loadAttendanceDetails(attendance.classId, attendance.objectId)
                            } else {
                                Log.e("Attendance", "更新缺勤失败: ${e.message}")
                            }
                        }
                    })
                } else {
                    Log.e("Attendance", "查询Attendance失败: ${e?.message}")
                }
            }
        })
    }


}
