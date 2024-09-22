package com.example.classtrack.data

import android.content.Context
import android.widget.Toast
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener

enum class UserType{
    TEACHER,
    STUDENT
}
class User(userType: UserType=UserType.STUDENT) :BmobUser() {
    val userType:String=userType.name
    var deviceId: String? = null  // 存储每个用户的设备标识符
}

//fun signUp(user:User,mContext: Context)
//{
//    user.signUp(object : SaveListener<User>() {
//        override fun done(currentUser: User?, ex: BmobException?) {
//            if (ex == null) {
//                Toast.makeText(mContext, "注册成功", Toast.LENGTH_LONG).show()
//            } else {
//                Toast.makeText(mContext, ex.message, Toast.LENGTH_LONG).show()
//            }
//        }
//    })
//}

