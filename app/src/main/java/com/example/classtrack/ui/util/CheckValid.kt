package com.example.classtrack.ui.util

fun loginCheckValid(userName:String,password:String):Int{
    return if(userName.isNotEmpty()){
        if(password.isNotEmpty())
        {
            3    //3 表示输入用户名，密码格式正常
        }
        else
        {
            2     //2 表示密码为空
        }
    }
    else
    {
        1         //1 表示 用户名为空
    }
}

fun registerCheckValid(userName: String,password1: String,password2: String):Int{
    if(userName.isNotEmpty())
    {
        return if(password1.isNotEmpty() && password2.isNotEmpty()) {
            if(password1==password2) {
                4 //正确格式
            } else {
                3 //表示两个密码不一致
            }
        } else {
            2   //表示有密码为空
        }
    }
    else
    {
        return 1  //  表示用户名为空
    }
}