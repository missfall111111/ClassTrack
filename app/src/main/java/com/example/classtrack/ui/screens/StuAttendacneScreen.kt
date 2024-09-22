package com.example.classtrack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import cn.bmob.v3.BmobUser
import com.example.classtrack.R
import com.example.classtrack.data.Attendance
import com.example.classtrack.ui.component.DrawerContent
import com.example.classtrack.ui.component.Qr_code_scanner
import com.example.classtrack.ui.navigation.StuRoute


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StuAttendanceScreen(
    navController: NavController, classObj: String,
    viewModel: StuAttendanceViewModel = hiltViewModel()
) {

    val attendances by viewModel.attendances.collectAsState()

    // 获取当前页面的生命周期
    val lifecycleOwner = LocalLifecycleOwner.current

    // 使用 DisposableEffect 监听生命周期
    DisposableEffect(lifecycleOwner) {
        // 创建生命周期观察者
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // 当页面重新可见时，重新加载数据
                viewModel.fetchAttendancesForClass(classObj)
            }
        }

        // 监听生命周期
        lifecycleOwner.lifecycle.addObserver(observer)

        // 当 Composable 销毁时移除观察者
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val currentUser: BmobUser? = BmobUser.getCurrentUser(BmobUser::class.java)
    val studentId = currentUser?.objectId

    ModalNavigationDrawer(drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController=navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Attendance Detail",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.surface)
                )
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
        { padding ->
            if (attendances.isEmpty()) {
                NoAttendance()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(attendances.reversed()) { attendance ->
                        if (studentId != null) {
                            AttendanceItem(attendance = attendance, studentId = studentId,
                                onClick = {
                                    if (attendance.checkInType == "QRCode")
                                        navController.navigate(StuRoute.SCANATTENDANCE.name)
                                    else navController.navigate(StuRoute.PASSCODE.name + "/${attendance.objectId}")
                                })
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NoAttendance(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.nature_people_24px),
            contentDescription = null,
            modifier = Modifier.size(250.dp)
        )

        Text(
            text = "No Attendance yet\n 😀",
            style = MaterialTheme.typography.titleLarge,  // 修改为更大的字体样式
            textAlign = TextAlign.Center,
            fontSize = 40.sp,
            lineHeight = 40.sp,
        )
    }
}

@Composable
fun AttendanceItem(attendance: Attendance, studentId: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardColors(contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surface  ,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary)
    ) {
        Box(){
            Image(painter = painterResource(id = R.drawable.attendance),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(185.dp),
                contentScale = ContentScale.Crop ,
                alpha = 0.7f)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // 日期与签到状态
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_calendar_today_24),
                            contentDescription = "签到日期",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = attendance.time,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 如果签到已结束，显示已结束状态
                    if (attendance.isEnded) {
                        Text(
                            text = "已结束",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 显示签到类型（QR码或口令）
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "签到类型: ${attendance.checkInType}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (attendance.checkInType == "Passcode") {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "口令签到",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 显示学生的签到情况
                val studentCheckIn = attendance.presentStudents.find { it.studentId == studentId }
                if (studentCheckIn != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "签到成功",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "签到成功，时间：${studentCheckIn.checkInTime}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 显示签到的地理位置信息
                    if (studentCheckIn.latitude != null && studentCheckIn.longitude != null) {
                        Text(
                            text = "签到位置：(${studentCheckIn.latitude}, ${studentCheckIn.longitude})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    Text(
                        text = "未签到",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 如果签到未结束，显示 "Enter" 按钮
                    if (!attendance.isEnded) {
                        Button(
                            onClick = onClick,
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                disabledContentColor = MaterialTheme.colorScheme.onErrorContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text("Enter")
                        }
                    }
                }
            }
        }
    }
}
