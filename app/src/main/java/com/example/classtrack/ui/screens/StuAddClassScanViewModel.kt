package com.example.classtrack.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.QueryListener
import cn.bmob.v3.listener.UpdateListener
import com.example.classtrack.data.Class
import com.example.classtrack.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class StuAddClassScanViewModel @Inject constructor():ViewModel() {

    fun handleScanResult(result: String) {
        try {
            // 解析二维码内容
            val jsonObject = JSONObject(result)
            val type = jsonObject.getString("type")
            val classId = jsonObject.getString("classId")

            // 检查是否是加课类型的二维码
            if (type == "join_class" && classId.isNotEmpty()) {
                joinClass(classId) // 调用加入班级的函数
            } else {
                Log.e("QRCode", "Invalid QR code type or empty classId")
            }
        } catch (e: JSONException) {
            Log.e("QRCode", "Failed to parse QR code: ${e.message}")
        }
    }

    private fun joinClass(classId: String) {
        val user = BmobUser.getCurrentUser(User::class.java)

        // 查询班级对象
        val query = BmobQuery<Class>()
        query.getObject(classId, object : QueryListener<Class>() {
            override fun done(classObj: Class?, e: BmobException?) {
                if (e == null && classObj != null) {
                    // 将当前用户加入班级的学生列表
                    val updatedStudents = classObj.students
                    if (!updatedStudents.contains(user.objectId)) {
                        updatedStudents.add(user.objectId)
                    } else {
                        Log.d("JoinClass", "You are already in this class")
                        return
                    }

                    // 更新班级对象
                    val updatedClass = Class(
                        className = classObj.className,
                        teacherId = classObj.teacherId,
                        students = updatedStudents
                    )
                    updatedClass.objectId = classObj.objectId // 设置对象ID以更新该班级

                    // 使用更新API
                    updatedClass.update(object : UpdateListener() {
                        override fun done(ex: BmobException?) {
                            if (ex == null) {
                                Log.d("JoinClass", "Successfully joined class")
//                                // 提示用户加入成功
//                                Toast.makeText(applicationContext, "加入班级成功", Toast.LENGTH_LONG).show()
                            } else {
                                Log.e("JoinClass", "Failed to update class: ${ex.message}")
//                                // 提示用户加入失败
//                                Toast.makeText(applicationContext, "加入班级失败: ${ex.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    })
                } else {
                    Log.e("JoinClass", "Failed to query class: ${e?.message}")
//                    Toast.makeText(applicationContext, "查询班级失败: ${e?.message}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

//    private fun joinClass(classId: String) {
//        val user = BmobUser.getCurrentUser(User::class.java)
//
//        // 查询班级
//        val query = BmobQuery<Class>()
//        query.getObject(classId, object : QueryListener<Class>() {
//            override fun done(classObj: Class?, e: BmobException?) {
//                if (e == null && classObj != null) {
//                    // 将当前用户加入班级的学生列表
//                    val updatedStudents = classObj.students
//                    if (!updatedStudents.contains(user.objectId)) {
//                        updatedStudents.add(user.objectId)
//                    }
//
//                    // 更新班级学生列表
//                    classObj.students = updatedStudents
//                    classObj.update(object : UpdateListener() {
//                        override fun done(e: BmobException?) {
//                            if (e == null) {
//                                Log.d("JoinClass", "Successfully joined class")
//                                // TODO: 更新UI，提示用户加入成功
//                            } else {
//                                Log.e("JoinClass", "Failed to update class: ${e.message}")
//                            }
//                        }
//                    })
//                } else {
//                    Log.e("JoinClass", "Failed to query class: ${e?.message}")
//                }
//            }
//        })
//    }
}