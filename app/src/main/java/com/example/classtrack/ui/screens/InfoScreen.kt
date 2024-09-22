package com.example.classtrack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.classtrack.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(navController: NavController, viewModel: InfoViewModel = hiltViewModel()) {
    val user by viewModel.user.observeAsState()

    var username by remember { mutableStateOf(user?.username ?: "") }
    var mobilePhoneNumber by remember { mutableStateOf(user?.deviceId ?: "") }
    var email by remember { mutableStateOf(user?.deviceId ?: "") }

    // 当用户信息加载后，更新状态
    LaunchedEffect(user) {
        user?.let {
            username = it.username ?: ""
            mobilePhoneNumber = it.mobilePhoneNumber ?: ""
            email = it.email ?: ""
        }
    }

    val scope= rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,             // 背景颜色
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer, // 滚动时的背景颜色
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,  // 导航图标颜色
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,          // 标题颜色
                    actionIconContentColor =MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(painter = painterResource(id = R.drawable.images),
                contentDescription =null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)  // 设置图片大小
                    .clip(CircleShape)) // 将图片裁剪为圆形


            // 不可修改的 userType
            Text(text = "User Type: ${user?.userType}",
                style = MaterialTheme.typography.bodyLarge,)

            Spacer(modifier = Modifier.height(8.dp))

            // 可修改的用户名
            TextField(
                value = username,
                onValueChange = { newUsername -> username = newUsername },
                label = { Text("Username") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 可修改的手机号
            TextField(
                value = mobilePhoneNumber,
                onValueChange = { newPhoneNumber -> mobilePhoneNumber = newPhoneNumber },
                label = { Text("Mobile Phone") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 可修改的邮箱
            TextField(
                value = email,
                onValueChange = { newEmail -> email = newEmail },
                label = { Text("Email") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 保存按钮
            Button(onClick = {
                viewModel.saveUserInfo(
                    username = username,
                    mobilePhoneNumber = mobilePhoneNumber,
                    email,
                    scope =scope,
                    snackbarHostState = snackbarHostState
                )
            }) {
                Text(text = "Save")
            }
        }
    }

}
