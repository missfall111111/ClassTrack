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
import com.example.classtrack.data.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class SignUpState(){
    data object IDLE:SignUpState()    // 表示还没开始登录
    data object Loading:SignUpState()
    data object Success:SignUpState()
    data object Failed:SignUpState()
}

@HiltViewModel
class SignUpViewModel @Inject constructor():ViewModel()
{

    private val _signUpState= MutableStateFlow<SignUpState>(SignUpState.IDLE)
    val signUpState: StateFlow<SignUpState> =_signUpState


    fun signUp(username:String,
               password:String,
               userType: UserType,
               snackbarHostState: SnackbarHostState,
               scope: CoroutineScope,
               context: Context
    )
    {
        _signUpState.value=SignUpState.Loading
        val user = User(userType=userType)
        user.username = username
        user.setPassword(password)
        user.signUp(object : SaveListener<User>() {
            override fun done(currentUser: User?, ex: BmobException?) {
                if (ex == null) {
//                Toast.makeText(mContext, "登录成功", Toast.LENGTH_LONG).show()
                    _signUpState.value=SignUpState.Success
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.sign_up_success),
                            duration = SnackbarDuration.Short
                        )
                    }

                } else {
//                Toast.makeText(mContext, ex.message, Toast.LENGTH_LONG).show()
                    _signUpState.value=SignUpState.Failed
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

    fun dismiss(){
        _signUpState.value=SignUpState.IDLE
    }
}