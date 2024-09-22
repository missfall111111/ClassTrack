package com.example.classtrack.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.classtrack.R
import com.example.classtrack.data.Attendance
import com.example.classtrack.data.Class
import com.example.classtrack.ui.component.DrawerContent
import com.example.classtrack.ui.component.People
import com.example.classtrack.ui.navigation.TeaRoute
import com.example.classtrack.ui.util.SwipeActionsLeft
import com.example.classtrack.ui.util.SwipeActionsRight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeaClassDetailScreen(
    viewModel: TeaClassDetailViewModel = hiltViewModel(),
    navController: NavController,
    cLassObjId: String,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    ModalNavigationDrawer(drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController=navController)
        }
    ) {

        viewModel.fetchClassById(classId = cLassObjId)

        val isLoading by viewModel.isLoading.collectAsState()

        val context= LocalContext.current

        val scope = rememberCoroutineScope()

        val scaffoldState = rememberBottomSheetScaffoldState()

        val cLass by viewModel.classLiveData.observeAsState()

        val attendanceCreated by viewModel.attendanceCreated.observeAsState()


        // 监听attendanceCreated的变化
        LaunchedEffect(attendanceCreated) {
            when (attendanceCreated) {
                true -> {
                    Log.d("signInType","${viewModel.signInType}")
                    // 根据签到类型导航到不同页面


                    when (viewModel.signInType) {
                        "QRCode" -> navController.navigate(TeaRoute.QR.name + "/${cLassObjId}")
                        "Passcode" -> navController.navigate(TeaRoute.PASSCODE.name + "/${cLassObjId}/${viewModel.passcode}")
                    }

                    // 重置状态
                    viewModel.resetAttendanceCreated()

                }
                false -> {
                    Toast.makeText(context, "Failed to create attendance", Toast.LENGTH_SHORT).show()
                    // 重置状态
                    viewModel.resetAttendanceCreated()
                }
                null -> {
                    // 正在加载或未初始化时的状态处理

                }
            }
        }

        val attendanceList by viewModel.attendanceList.observeAsState(emptyList())

        LaunchedEffect(cLass?.objectId) {
            cLass?.let { viewModel.fetchAttendanceRecords(classId = it.objectId) }
        }

        BottomSheetScaffold(scaffoldState = scaffoldState,
            sheetContent = {
                StuSheetContent(cLass = cLass,
                    onADDStu = { navController.navigate(TeaRoute.ADDSTUDENT.name + "/${cLass?.objectId}") },
                    getStuName = {studentId -> viewModel.getStuName(stuObjId = studentId) },
                    actionOneClicked = {
                        viewModel.removeStudentFromClass(cLass?.objectId ?: "", it) { success ->
                            if (success) {
                                viewModel.fetchAttendanceRecords(classId = cLass?.objectId ?: "")
                                // 这里可以处理成功的逻辑，例如通知UI更新
                                Log.d("removeStudent", "Student removed successfully")
                            } else {
                                // 处理失败的逻辑
                                Log.e("removeStudent", "Failed to remove student")
                            }
                        }
                    })
            },
            sheetPeekHeight = 120.dp,
            topBar = { TeaClassDetailTopAppBar(navController = navController) },
            containerColor = MaterialTheme.colorScheme.primary,
        ) {
            if(isLoading){
                Column (modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally){
                    CircularProgressIndicator()
                    Text(text = "Loading . . . ", color = MaterialTheme.colorScheme.onBackground)
                }
            }
            else{
                AttendanceList(
                    onQRcode =
                    { viewModel.createAttendance(cLass?.objectId ?: "", checkInType = "QRCode")
                    },
                    onPasscodeSelected =   {viewModel.generateRandomPasscode()
                        viewModel.createAttendance(cLass?.objectId ?: "",
                            checkInType = "Passcode",
                            passcode = viewModel.passcode) },
                    attendanceList =attendanceList,
                    onDetailsClick = {navController.navigate(TeaRoute.ATTENDANCEDTEAIL.name+"/$cLassObjId/$it")
                        Log.d("Attendance","it:$it")}
                )
            }
        }
    }
}


@Composable
fun AttendanceList(
    onQRcode:()->Unit,
    onPasscodeSelected: () -> Unit,
    onDetailsClick: (attendanceId: String) -> Unit,
    attendanceList: List<Attendance>?
){

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(floatingActionButton =
    { NewAttendanceFloatingActionBar(onClick = {showDialog=true},
        modifier = Modifier.offset(y = (-90).dp))},
        floatingActionButtonPosition = FabPosition.Start,
        containerColor = MaterialTheme.colorScheme.primaryContainer) {
        if (showDialog) {
            CreateAttendanceDialog(
                onDismissRequest = { showDialog = false },
                onQRCodeSelected = {
                    showDialog = false
                    // 执行二维码签到的逻辑
                    Log.d("Attendance", "二维码签到选中")
                    onQRcode()

                },
                onPasscodeSelected = {
                    showDialog = false
                    // 执行口令签到的逻辑
                    onPasscodeSelected()
                    Log.d("Attendance", "口令签到选中")
                }
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(660.dp)
                .padding(it)
        ) {
            items(attendanceList.orEmpty().reversed()) { attendance ->
                AttendanceListItem(attendance = attendance, onDetailsClick = onDetailsClick)
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
            }
        }
    }
}

@Composable
fun AttendanceListItem(
    attendance: Attendance,
    onDetailsClick: (attendanceId: String) -> Unit // 传递点击事件回调
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.primaryContainer),
            disabledContentColor = MaterialTheme.colorScheme.inverseSurface,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Attendance Time: ${attendance.time}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Attendance Method: ${attendance.checkInType}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Present Student Number: ${attendance.presentStudents.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 15.sp
            )
            Button(
                onClick = { onDetailsClick(attendance.objectId) },
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(text = "Detail")
            }
        }
    }
}



@Composable
fun NewAttendanceFloatingActionBar(modifier: Modifier=Modifier,
                                   onClick: () -> Unit){
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(90.dp),
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Column {
            Icon(
                painter = painterResource(id = R.drawable.baseline_assignment_add_24),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(text = "New Attendance", textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun CreateAttendanceDialog(
    onDismissRequest: () -> Unit,
    onQRCodeSelected: () -> Unit,
    onPasscodeSelected: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Attendance method")
        },
        text = {
            Column {
                Text(text = "Choose a method to launch attendance")
            }
        },
        confirmButton = {
            TextButton(onClick = onQRCodeSelected) {
                Text("QR code ")
            }
        },
        dismissButton = {
            TextButton(onClick = onPasscodeSelected) {
                Text("Password ")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeaClassDetailTopAppBar(navController: NavController) {
    TopAppBar(title = {
        Text(
            text = "Attendance Overlook",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarColors(containerColor = MaterialTheme.colorScheme.inversePrimary,
            scrolledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.secondary,
            navigationIconContentColor = MaterialTheme.colorScheme.tertiary )
    )
}

@Composable
fun StuSheetContent(modifier: Modifier = Modifier,
                    cLass: Class?,
                    onADDStu: () -> Unit,
                    getStuName:suspend (String)->String?,
                    actionOneClicked:(String)->Unit) {
    Scaffold(modifier = Modifier.height(550.dp),
        floatingActionButton = { NewStudentFloatingActionBar(onClick = onADDStu)},
        containerColor = MaterialTheme.colorScheme.primaryContainer) {
        Column(modifier = modifier.fillMaxWidth().padding(it)) {
            Text(
                text = "My Students",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
            if (cLass != null) {
                if (cLass.students.isEmpty()) {
                    Box(
                        modifier = Modifier.height(500.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Haven't had a student yet",
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        items(cLass.students){
                                studentId->
                            var stuName by remember { mutableStateOf<String?>(null) }

                            // 每个学生都使用 LaunchedEffect 等待获取名字
                            LaunchedEffect(studentId) {
                                stuName = getStuName(studentId)
                                Log.d("stuName", "Fetched: $stuName")
                            }

                            if (stuName != null) {
                                StudentListItem(stuName = stuName!!, actionOneClicked = {actionOneClicked(studentId)})
                            } else {
                                Text(text = "Loading...")  // 如果名字未加载完成，显示 Loading...
                            }
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewStudentFloatingActionBar(onClick: () -> Unit,modifier: Modifier=Modifier) {
    FloatingActionButton(
        onClick = onClick,
        modifier =modifier.size(70.dp)
    ) {
        Column {
            Icon(
                People,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(text = "Get  Students", textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun StudentListItem(stuName: String,
                    actionOneClicked:()->Unit) {

    var isExpanded by remember { mutableStateOf(false) }
    SwipeActionsLeft(isExpanded = isExpanded, onChangedCard = {isExpanded=it},
        modifier = Modifier.height(80.dp).fillMaxWidth(0.95f).padding(vertical = 5.dp),
        actionOneImage = Icons.Default.Delete,
        actionOneColor = Color.Red,
        numberOfActions = 1,
        iconPadding = 16.dp,
        actionOneClicked = actionOneClicked,
        cardBackground = MaterialTheme.colorScheme.tertiary
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = stuName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Student Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        .padding(8.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 8.dp, horizontal = 16.dp),

        )
    }

}





