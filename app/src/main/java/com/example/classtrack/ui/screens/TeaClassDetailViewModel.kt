package com.example.classtrack.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.QueryListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import com.example.classtrack.data.Attendance
import com.example.classtrack.data.Class
import com.example.classtrack.data.PushResponse
import com.example.classtrack.data.User
import com.example.classtrack.data.UserType
import com.example.classtrack.ui.util.sendPushNotificationToAndroid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class TeaClassDetailViewModel @Inject constructor():ViewModel() {

    // 用于存储查询到的 Class 实体
    private val _class = MutableLiveData<com.example.classtrack.data.Class?>()
    val classLiveData: LiveData<com.example.classtrack.data.Class?> get() = _class

    private var _isLoading= MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 根据 classId 获取对应的 Class 实体
    fun fetchClassById(classId: String) {
        val query = BmobQuery<com.example.classtrack.data.Class>()
        query.getObject(classId, object : QueryListener<com.example.classtrack.data.Class>() {
            override fun done(classObj: Class?, e: BmobException?) {
                if (e == null) {
                    // 成功获取到 Class 实体
                    Log.d("YourViewModel","${_class.value}")
                    _class.value = classObj
                    _isLoading.value=false
                } else {
                    // 处理错误
                    Log.e("YourViewModel", "Error fetching class: ${e.message}")
                }
            }
        })
    }

    suspend fun getStuName(stuObjId: String): String? = suspendCoroutine { continuation ->
        BmobQuery<User>().getObject(stuObjId, object : QueryListener<User>() {
            override fun done(student: User?, e: BmobException?) {
                if (e == null && student != null) {
                    continuation.resume(student.username)  // 返回学生名字
                } else {
                    continuation.resume(null)  // 返回 null 表示失败
                }
            }
        })
    }
    var passcode: String? by mutableStateOf(null)

    // 随机生成一个口令
    fun generateRandomPasscode() {
        val chars = ('A'..'Z') + ('0'..'9')  // 生成包含字母和数字的字符列表
        passcode= (1..6).map { chars.random() }.joinToString("") // 生成6位随机口令
    }

    private val _attendanceCreated = MutableLiveData<Boolean?>()
    val attendanceCreated: LiveData<Boolean?> = _attendanceCreated

    var signInType: String? = null

    fun createAttendance(classId: String, checkInType: String, passcode: String? = null,isEnded:Boolean=false) {

        this.signInType = checkInType

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedTime = formatter.format(Date(System.currentTimeMillis()))

        // 启动一个协程进行异步操作
        viewModelScope.launch {
            val attendance = Attendance(
                classId = classId,
                time = formattedTime,
                checkInType = checkInType,
                passcode = passcode,
                isEnded = isEnded
            )

            // 执行数据库操作
            attendance.save(object : SaveListener<String>() {
                override fun done(objectId: String?, e: BmobException?) {
                    if (e == null) {
                        // 新建成功，通知UI
                        _attendanceCreated.value = true

                        notifyAllStudents(classId, attendance)
                    } else {
                        // 失败，处理错误
                        _attendanceCreated.value = false
                    }
                }
            })
        }
    }

    fun resetAttendanceCreated() {
        _attendanceCreated.value = null // 重置状态
        signInType = null // 同时重置签到类型
    }


    // 用于存储签到记录的 LiveData
    private val _attendanceList = MutableLiveData<List<Attendance>?>()
    val attendanceList: MutableLiveData<List<Attendance>?> get() = _attendanceList

    // 获取某个班级的所有签到记录
    fun fetchAttendanceRecords(classId: String) {
        // 创建查询对象
        val query = BmobQuery<Attendance>()

        // 设置查询条件，获取对应班级的签到记录
        query.addWhereEqualTo("classId", classId)

        // 异步查询数据
        query.findObjects(object : FindListener<Attendance>() {
            override fun done(attendanceList: List<Attendance>?, e: BmobException?) {
                if (e == null && attendanceList != null) {
                    // 成功获取数据，更新 LiveData
                    _attendanceList.value = attendanceList
                } else {
                    _attendanceList.value = emptyList()
                    // 查询失败，打印错误日志
                    Log.e("AttendanceViewModel", "Failed to fetch attendance records", e)
                }
            }
        })
    }


    private fun fetchStudentDeviceIds(studentIds: List<String>, onSuccess: (List<String>) -> Unit, onError: (Exception) -> Unit) {
        val query = BmobQuery<User>()
        query.addWhereContainedIn("objectId", studentIds) // 这里用objectId对应学生ID
        query.findObjects(object : FindListener<User>() {
            override fun done(users: List<User>?, e: BmobException?) {
                if (e == null && users != null) {
                    val deviceIds = users.mapNotNull { it.deviceId }
                    onSuccess(deviceIds)
                } else {
                    onError(e ?: Exception("无法查询学生设备ID"))
                }
            }
        })
    }

    fun notifyAllStudents(classId: String, attendance: Attendance) {
        // 获取班级的学生列表
        fetchClassById(classId) // 确保_class.value 包含最新的班级数据

        val studentIds = _class.value?.students ?: emptyList()

        // 获取所有学生的设备ID
        fetchStudentDeviceIds(studentIds, { deviceIds ->
            // 成功获取到设备ID，准备发送推送通知
            sendAttendanceNotification(deviceIds, attendance)
        }, { error ->
            Log.e("TeaClassDetailViewModel", "Error fetching student device IDs: ${error.message}")
        })
    }

    private fun sendAttendanceNotification(deviceIds: List<String>, attendance: Attendance) {
        // 推送通知的标题和内容
        val notificationTitle = "签到通知"
        val notificationBody = "班级${attendance.classId}的签到已创建，类型为${attendance.checkInType}，请尽快签到！"

        // 推送服务所需的密钥和认证信息
        val appKey: Long = 334958991 // 替换为你的真实 appKey
        val accessKeyId = "LTAI5tLCHvTHiXmUe2GHG7sR"
        val accessKeySecret = "kXrJGKwF6rxgitCyTf0eeB2rRxZmYJ"

        // 使用协程进行异步推送操作
        viewModelScope.launch {
            try {
                // 调用推送函数，将必要参数传递进去
                sendPushNotificationToAndroid(
                    appKey = appKey,
                    accessKeyId = accessKeyId,
                    accessKeySecret = accessKeySecret,
                    deviceIds = deviceIds,  // 传递多个设备ID
                    title = notificationTitle,
                    body = notificationBody
                )
            } catch (e: Exception) {
                Log.e("TeaClassDetailViewModel", "Error sending push notifications: ${e.message}")
            }
        }
    }


    fun removeStudentFromClass(classId: String, studentId: String, onComplete: (Boolean) -> Unit) {
        // 查询班级对象
        val query = BmobQuery<Class>()
        query.getObject(classId, object : QueryListener<Class>() {
            override fun done(classObject: Class?, ex: BmobException?) {
                if (ex == null && classObject != null) {

                    val newClass= com.example.classtrack.data.Class(
                        className = classObject.className,
                        teacherId = classObject.teacherId,
                        students = classObject.students
                    )
                    // 移除学生 ID
                    newClass.students.remove(studentId)
                    newClass.objectId=classObject.objectId
                    // 更新班级
                    newClass.update(object : UpdateListener() {
                        override fun done(updateEx: BmobException?) {
                            if (updateEx == null) {
                                onComplete(true)  // 更新成功
                            } else {
                                onComplete(false)  // 更新失败
                            }
                        }
                    })
                } else {
                    onComplete(false)  // 查询失败
                }
            }
        })
    }


}