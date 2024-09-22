package com.example.classtrack.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.example.classtrack.R
import com.example.classtrack.data.User
import com.example.classtrack.data.UserType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.activity.viewModels
import cn.bmob.v3.listener.UpdateListener
import com.alibaba.sdk.android.push.CloudPushService
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import com.example.classtrack.ui.util.Sp
import com.example.classtrack.ui.util.setSharedPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState(){
    data object IDLE:LoginState()    // 表示还没开始登录
    data object Loading:LoginState()
    data object Success:LoginState()
    data object Failed:LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor():ViewModel(){
    private val _loginState=MutableStateFlow<LoginState>(LoginState.IDLE)
    val loginState: StateFlow <LoginState> =_loginState

    var userType= mutableStateOf("")

    fun login(username:String,
              password:String,
              snackbarHostState: SnackbarHostState,
              scope: CoroutineScope,
              context: Context
    ){
        _loginState.value=LoginState.Loading
        val user = User()
        user.username = username
        user.setPassword(password)
        user.login(object : SaveListener<User>() {

            override fun done(currentUser: User?, ex: BmobException?) {
                if (ex == null) {
                    _loginState.value=LoginState.Success
                    userType.value= currentUser?.userType ?: ""
                    setSharedPreference(context = context, key = Sp.USERNAME, value = username)
                    setSharedPreference(context=context, key = Sp.PASSWORD, value = password)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.login_success),
                            duration = SnackbarDuration.Short
                        )
                    }

                    if (currentUser != null) {
                        updateDeviceId(user=currentUser)
                    }
                }
                else {
                    _loginState.value=LoginState.Failed
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = ex.message +"\n"+context.getString(R.string.login_failed),
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
        )
    }


    fun updateDeviceId(user: User) {
        // 初始化阿里云推送服务
        val pushService: CloudPushService = PushServiceFactory.getCloudPushService()

        // 获取设备ID
        val deviceId = pushService.deviceId
        if (deviceId != null) {
            Log.d("PushService", "获取到设备ID: $deviceId")

            // 更新用户的deviceId
            user.deviceId = deviceId
            user.update(object : UpdateListener() {
                override fun done(e: BmobException?) {
                    if (e == null) {
                        Log.d("Bmob", "用户设备ID更新成功")
                    } else {
                        Log.e("Bmob", "用户设备ID更新失败: ${e.message}")
                    }
                }
            })
        } else {
            Log.e("PushService", "无法获取设备ID")
        }
    }
}