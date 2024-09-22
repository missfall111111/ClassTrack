package com.example.classtrack.data

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class StuClassesRepository {
    suspend fun getStuClasses(): List<Class>? {
        return fetchStudentClasses() // 挂起函数调用
    }
}

suspend fun fetchStudentClasses(): List<Class>? = suspendCancellableCoroutine { continuation ->
    val currentUser: BmobUser? = BmobUser.getCurrentUser(BmobUser::class.java)
    val query = BmobQuery<Class>()

    // 查询学生ID是否包含在班级的学生列表中
    query.addWhereEqualTo("students", currentUser?.objectId ?: "")
    query.findObjects(object : FindListener<Class>() {
        override fun done(classes: List<Class>?, e: BmobException?) {
            if (e == null) {
                continuation.resume(classes)
            } else {
                continuation.resumeWithException(e)
            }
        }
    })
}