package com.example.classtrack.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.QueryListener
import cn.bmob.v3.listener.UpdateListener
import com.example.classtrack.data.Class
import com.example.classtrack.data.StuClassesRepository
import com.example.classtrack.data.User
import com.example.classtrack.data.UserType
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class StuClassesScreenViewModel @Inject constructor(private val repository:StuClassesRepository):ViewModel() {
    private var _classes = MutableStateFlow<List<Class>?>(listOf())
    val classes: StateFlow<List<Class>?> = _classes

    init {
        loadTeacherClasses()
    }

    fun loadTeacherClasses() {
        viewModelScope.launch {
            _classes.value =  repository.getStuClasses()
        }
    }

    suspend fun getTeacherName(teaObjId: String): String? = suspendCoroutine { cont ->
        val query = BmobQuery<User>()
        query.getObject(teaObjId, object : QueryListener<User>() {
            override fun done(teacher: User?, e: BmobException?) {
                if (e == null && teacher != null && teacher.userType == UserType.TEACHER.name) {
                    cont.resume(teacher.username)
                } else {
                    cont.resume(null)
                }
            }
        })
    }


//    private fun joinClass(classId: String) {
//        // 查询班级对象
//        val query = BmobQuery<Class>()
//        query.getObject(classId, object : QueryListener<Class>() {
//            override fun done(classObj: Class?, e: BmobException?) {
//                if (e == null && classObj != null) {
//                    val currentUser: User? = BmobUser.getCurrentUser(User::class.java)
//                    if (currentUser != null) {
//                        // 如果当前学生不在列表中
//                        if (!classObj.students.contains(currentUser.objectId)) {
//                            // 创建一个新的列表，将现有学生和当前学生的ID合并
//                            val updatedStudents = classObj.students + currentUser.objectId
//
//                            // 使用新列表替换原有的 students
//                            classObj.students = updatedStudents
//
//                            // 更新班级对象
//                            classObj.update(object : UpdateListener() {
//                                override fun done(e: BmobException?) {
//                                    if (e == null) {
//                                        println("成功加入班级！")
//                                    } else {
//                                        e.printStackTrace()
//                                    }
//                                }
//                            })
//                        }
//                    }
//                } else {
//                    e?.printStackTrace()
//                }
//            }
//        })
//    }

}