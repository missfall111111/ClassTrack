package com.example.classtrack.ui.screens

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.example.classtrack.R
import com.example.classtrack.data.User
import com.example.classtrack.ui.util.Sp
import com.example.classtrack.ui.util.setSharedPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashState(){
    data object IDLE:SplashState()    // 表示还没开始登录
    data object Loading:SplashState()
    data object Success:SplashState()
    data object Failed:SplashState()
}

@HiltViewModel
class SplashViewModel @Inject constructor():ViewModel(){

    private val _splashState= MutableStateFlow<SplashState>(SplashState.IDLE)
    val splashState: StateFlow<SplashState> =_splashState

    var userType= mutableStateOf("")

    fun login(username:String,
              password:String
    ){
        _splashState.value=SplashState.Loading
        val user = User()
        user.username = username
        user.setPassword(password)
        user.login(object : SaveListener<User>() {

            override fun done(currentUser: User?, ex: BmobException?) {
                if (ex == null) {
                    userType.value= currentUser?.userType ?: ""
                    _splashState.value=SplashState.Success
                }
                else {
                    _splashState.value=SplashState.Failed
                }
            }
        }
        )
    }
}