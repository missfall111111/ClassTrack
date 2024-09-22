package com.example.classtrack.ui.screens

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UpdateListener
import com.example.classtrack.R
import com.example.classtrack.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class InfoViewModel @Inject constructor():ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    init {
        loadUserData()
    }

    // 加载当前用户数据
    private fun loadUserData() {
        val currentUser = BmobUser.getCurrentUser(User::class.java)
        _user.value = currentUser
    }


    // 保存用户信息
    fun saveUserInfo(username:String,
                     mobilePhoneNumber:String,
                     email:String,
                     scope: CoroutineScope,
                     snackbarHostState: SnackbarHostState,) {
        // 获取当前用户对象
        val currentUser = BmobUser.getCurrentUser(User::class.java)

        if (currentUser != null) {
            // 更新用户信息
            currentUser.username = username
            currentUser.mobilePhoneNumber = mobilePhoneNumber
            currentUser.email = email

            // 调用 Bmob 的更新方法
            currentUser.update(object : UpdateListener() {
                override fun done(e: BmobException?) {
                    if (e == null) {
                        // 更新成功，可以做出成功的提示或导航
                        Log.d("UserProfile", "User info updated successfully.")
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "User info updated successfully.",
                                duration = SnackbarDuration.Short
                            )
                        }
                    } else {
                        // 更新失败，可以提示用户错误信息
                        Log.e("UserProfile", "Failed to update user info: ${e.message}")
                    }
                }
            })
        } else {
            // 当前用户未登录或无法获取，提示用户
            Log.e("UserProfile", "No current user found, please log in.")
        }
    }
}