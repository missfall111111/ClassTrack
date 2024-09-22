package com.example.classtrack.data

import android.os.Parcelable
import cn.bmob.v3.BmobObject
import kotlinx.parcelize.Parcelize

data class Class(
    val className: String,
    val teacherId: String,
    var students: MutableList<String> = mutableListOf() // 学生ID列表
): BmobObject()

