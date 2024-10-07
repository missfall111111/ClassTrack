package com.example.classtrack.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import com.example.classtrack.data.Attendance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeaPassCodeScreen(navController: NavController,
                      classId:String,
                      passCode:String){

    DisposableEffect(Unit) {
        onDispose {
           endAttendanceByClassId(classId=classId)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("口令签到") },
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(32.dp)
                ) {
                    // 显示生成的口令
                    Text(
                        text = passCode,
                        style = MaterialTheme.typography.displayLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 提示信息
                Text(
                    text = "Sign In with this passcode",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


// 查询并更新最后一个签到状态为已结束
fun endAttendanceByClassId(classId: String) {
    val query = BmobQuery<Attendance>()
    query.addWhereEqualTo("classId", classId)
    query.findObjects(object : FindListener<Attendance>() {
        override fun done(attendanceList: List<Attendance>?, e: BmobException?) {
            if (e == null && !attendanceList.isNullOrEmpty()) {
                // 获取最后一个签到记录
                val oldAttendance = attendanceList.last()

                if(oldAttendance.isEnded==false){
                    val updatedAttendance= Attendance(classId=oldAttendance.classId,
                        checkInType = oldAttendance.checkInType,
                        passcode = oldAttendance.passcode,
                        presentStudents = oldAttendance.presentStudents,
                        time = oldAttendance.time,
                        isEnded = true)
                    updatedAttendance.objectId=oldAttendance.objectId

                    // 更新最后一个签到的状态
                    updatedAttendance.update(object : UpdateListener() {
                        override fun done(e: BmobException?) {
                            if (e == null) {
                                // 更新成功
                                Log.d("Attendance", "签到状态已更新为已结束")
                            } else {
                                // 更新失败
                                Log.e("Attendance", "更新签到状态失败: ${e.message}")
                            }
                        }
                    })
                }
                else{
                    return
                }
            } else {
                // 查询失败或未找到签到记录
                Log.e("Attendance", "查询失败或无记录: ${e?.message}")
            }
        }
    })
}
