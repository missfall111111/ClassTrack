package com.example.classtrack.data

import android.util.Log
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class TeaClassesRepository @Inject constructor() {

    suspend fun getTeacherClasses(): List<Class>? {
        return fetchTeacherClasses() // 挂起函数调用
    }
}



suspend fun fetchTeacherClasses(): List<Class>? = suspendCancellableCoroutine { continuation ->
    val currentUser: BmobUser? = BmobUser.getCurrentUser(BmobUser::class.java)
    val query = BmobQuery<Class>()
//    Log.d("getTeacherClass ","Start")
    query.addWhereEqualTo("teacherId", currentUser?.objectId ?: "")
    query.findObjects(object : FindListener<Class>() {
        override fun done(classes: List<Class>?, e: BmobException?) {
            if (e == null) {
                continuation.resume(classes)
//                Log.d("getTeacherClass ","End")
            } else {
                continuation.resumeWithException(e)
//                Log.d("getTeacherClass ","Error")
            }
        }
    })
}