package com.example.classtrack.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.example.classtrack.data.Attendance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class StuAttendanceViewModel @Inject constructor() :ViewModel(){

    private val _attendances = MutableStateFlow<List<Attendance>>(emptyList())
    val attendances: StateFlow<List<Attendance>> = _attendances

    fun fetchAttendancesForClass(classId: String) {
        // 查询Attendance表中指定班级(classId)的签到记录
        val query = BmobQuery<Attendance>()
        query.addWhereEqualTo("classId", classId)
        query.findObjects(object : FindListener<Attendance>() {
            override fun done(attendanceList: MutableList<Attendance>?, e: BmobException?) {
                if (e == null && attendanceList != null) {
                    // 更新状态流，通知UI层
                    _attendances.value = attendanceList
                } else {
                    // 处理错误
                    Log.e("StuAttendanceViewModel", "Error fetching attendances: ${e?.message}")
                }
            }
        })
    }
}