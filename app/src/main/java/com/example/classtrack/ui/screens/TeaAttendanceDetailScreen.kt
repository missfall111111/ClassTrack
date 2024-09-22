package com.example.classtrack.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.classtrack.data.Attendance
import com.example.classtrack.data.StudentCheckIn
import com.example.classtrack.ui.component.BottomBar
import com.example.classtrack.ui.component.DrawerContent
import com.example.classtrack.ui.util.SwipeActionsRight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeaAttendanceDetailScreen(
    navController: NavController,
    classId: String,
    attendanceId: String,
    viewModel: TeaAttendanceDetailViewModel = hiltViewModel()
) {

    val attendanceDetails by viewModel.attendanceDetails.collectAsState()
    val studentNameMap by viewModel.studentNameMap.collectAsState() // 订阅 studentNameMap
    val isStudentNameMapLoaded by viewModel.isStudentNameMapLoaded.collectAsState()
    // 使用 SideEffect 或 LaunchedEffect 来确保参数在首次加载时传递给 ViewModel
    LaunchedEffect(Unit) {
        viewModel.loadAttendanceDetails(classId, attendanceId) // UI 传递参数
    }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    ModalNavigationDrawer(drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController=navController)
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Attendance Detail") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.secondary,
                        navigationIconContentColor = MaterialTheme.colorScheme.tertiary
                    )
                )
            },
            bottomBar = { BottomBar(content = "Your Attendance") },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) { it ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                if (!isStudentNameMapLoaded) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (attendanceDetails.isNotEmpty()) {
                    AttendanceDetailsContent(
                        attendanceDetails = attendanceDetails,
                        studentNameMap = studentNameMap,
                        actionOneClicked = {viewModel.markStudentAsAbsent(studentCheckIn = it,attendanceId=attendanceId)},
                        actionTwoClicked = {viewModel.markStudentAsPresent(studentCheckIn = it,attendanceId=attendanceId)}
                    )
                } else {
                    Text("No data", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}


@Composable
fun AttendanceDetailsContent(
    attendanceDetails: List<StudentCheckIn>,
    studentNameMap: Map<String, String>, // 添加这个参数来获取学生名字
    actionOneClicked :(StudentCheckIn)->Unit,
    actionTwoClicked :(StudentCheckIn)->Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(attendanceDetails) { studentCheckIn ->
            val studentName =
                studentNameMap[studentCheckIn.studentId] ?: "Stranger" // 根据 studentId 获取名字
            Log.d("Attendance", "Student name: $studentName")
            var isExpanded by remember { mutableStateOf(false) }
            SwipeActionsRight(isExpanded =isExpanded ,
                onChangedCard = {isExpanded=it},
                actionOneImage = Icons.Default.Person,
                actionTwoImage = Icons.Default.Person,
                actionOneColor = Color.Red,
                actionTwoColor = Color.Green,
                actionOneClicked = {actionOneClicked(studentCheckIn)},
                actionTwoClicked = {actionTwoClicked(studentCheckIn)},
                actionOneText = "Absent",
                actionTwoText = "Present",
                cardBackground = MaterialTheme.colorScheme.inversePrimary) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.elevatedCardElevation(4.dp),

                    ) {
                    Column(
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.inversePrimary)
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Student: $studentName",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Attendance: ${studentCheckIn.checkInTime}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (studentCheckIn.checkInTime == "未签到") MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (studentCheckIn.latitude != null && studentCheckIn.longitude != null) {
                            Text(
                                text = "Position: ${studentCheckIn.latitude}, ${studentCheckIn.longitude}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

