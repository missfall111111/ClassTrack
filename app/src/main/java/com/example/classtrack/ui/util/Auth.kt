package com.example.classtrack.ui.util

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.example.classtrack.R
import com.example.classtrack.data.User
import com.example.classtrack.data.UserType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


fun signIn(username:String,
           password:String,
           userType: UserType,
           snackbarHostState: SnackbarHostState,
           scope: CoroutineScope,
           context: Context)
{
    val user = User(userType=userType)
    user.username = username
    user.setPassword(password)
    user.signUp(object : SaveListener<User>() {
        override fun done(currentUser: User?, ex: BmobException?) {
            if (ex == null) {
//                Toast.makeText(mContext, "登录成功", Toast.LENGTH_LONG).show()
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.sign_up_success),
                        duration = SnackbarDuration.Short
                    )
                }

            } else {
//                Toast.makeText(mContext, ex.message, Toast.LENGTH_LONG).show()
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = ex.message ?: context.getString(R.string.sign_up_failed),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
    )
}


fun loginIn(username:String,
            password:String,
            snackbarHostState: SnackbarHostState,
            scope: CoroutineScope,
            context: Context){
    val success= mutableStateOf(false)
    val user = User()
    user.username = username
    user.setPassword(password)
    success.value=false
    user.login(object : SaveListener<User>() {

            override fun done(currentUser: User?, ex: BmobException?) {
                if (ex == null) {
//                Toast.makeText(mContext, "登录成功", Toast.LENGTH_LONG).show()
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.login_success),
                            duration = SnackbarDuration.Short
                        )
                    }
                }
                else {
//                Toast.makeText(mContext, ex.message, Toast.LENGTH_LONG).show()
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = ex.message +context.getString(R.string.login_failed),
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    )
}