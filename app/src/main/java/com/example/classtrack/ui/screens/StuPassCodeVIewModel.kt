package com.example.classtrack.ui.screens

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.QueryListener
import cn.bmob.v3.listener.UpdateListener
import com.example.classtrack.data.Attendance
import com.example.classtrack.data.StudentCheckIn
import com.example.classtrack.ui.util.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StuPassCodeVIewModel @Inject constructor() :ViewModel(){

    private val _isPasscodeValid = MutableStateFlow(false)  // 初始值为 false
    val isPasscodeValid: StateFlow<Boolean> = _isPasscodeValid


    // ViewModel 中使用 MutableStateFlow 来追踪 isPasscodeIncorrect 状态
    private val _isPasscodeIncorrect = MutableStateFlow(false)
    val isPasscodeIncorrect: StateFlow<Boolean> = _isPasscodeIncorrect


    fun submitPasscode(inputPasscode: String, attendanceObjectId: String,context: Context) {
        viewModelScope.launch {
            val query = BmobQuery<Attendance>()
            query.getObject(attendanceObjectId, object : QueryListener<Attendance>() {
                override fun done(attendance: Attendance?, e: BmobException?) {
                    if (e == null && attendance != null && attendance.passcode == inputPasscode) {
                        _isPasscodeValid.value = true  // passcode 验证成功，修改状态

                        updatePresentStudents(attendance = attendance, context = context)
                        Log.d("submit","success")
                    } else {
                        _isPasscodeIncorrect.value = true  // 更新为 true 来触发状态改变
                    }
                }
            })
        }
    }

    fun resetPasscodeIncorrect() {
        _isPasscodeIncorrect.value = false
    }


    private fun getCurrentStudentId(): String {
        // 获取当前学生的ID（从登录信息或用户数据中获取）
        val currentUser: BmobUser? = BmobUser.getCurrentUser(BmobUser::class.java)
        return currentUser?.objectId ?: ""

    }

    private fun updatePresentStudents(attendance: Attendance,context:Context) {
        val studentId = getCurrentStudentId()
        val locationHelper = LocationHelper(context)

        locationHelper.getCurrentLatitude { latitude ->
            locationHelper.getCurrentLongitude { longitude ->
                val checkInTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(
                    Date()
                )
                val studentCheckIn = StudentCheckIn(
                    studentId = studentId,
                    checkInTime = checkInTime,
                    latitude = latitude,
                    longitude = longitude
                )

                val currentStudent=attendance.presentStudents
                if (!currentStudent.contains(studentCheckIn)) {
                    currentStudent.add(studentCheckIn)

                    val updatedAttendance=Attendance(classId = attendance.classId,
                        time = attendance.time,
                        checkInType = attendance.checkInType,
                        passcode = attendance.passcode,
                        presentStudents = currentStudent,
                        isEnded = attendance.isEnded)

                    updatedAttendance.objectId=attendance.objectId
                    updatedAttendance.update(object : UpdateListener() {
                        override fun done(e: BmobException?) {
                            if (e == null) {
                                Log.d("StuCheckIn", "签到成功")
                                _isPasscodeValid.value = true
                            } else {
                                Log.e("StuCheckIn", "签到更新失败: ${e.message}")
                            }
                        }
                    })
                } else {
                    Log.d("StuCheckIn", "已经签到过了")
                }
            }
        }
    }



}