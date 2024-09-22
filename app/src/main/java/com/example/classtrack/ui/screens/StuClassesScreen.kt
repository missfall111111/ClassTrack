package com.example.classtrack.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import cn.bmob.v3.BmobUser
import com.example.classtrack.R
import com.example.classtrack.data.Class
import com.example.classtrack.ui.component.BottomBar
import com.example.classtrack.ui.component.DrawerContent
import com.example.classtrack.ui.component.Qr_code_scanner
import com.example.classtrack.ui.navigation.StuRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bouncycastle.math.raw.Mod

@Composable
fun StuClassesScreen(
    navController: NavController,
    viewModel: StuClassesScreenViewModel = hiltViewModel()
) {
    Log.d("Navigation stack","Here is StuClass Screen")

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val classes by viewModel.classes.collectAsState()

    val currentUser: BmobUser? = BmobUser.getCurrentUser(BmobUser::class.java)

    val context = LocalContext.current


    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        if (cameraGranted) {
            Log.d("msg", "Camera access granted")
        } else {
            Log.d("msg", "Camera access denied")
        }

        if (locationGranted) {
            Log.d("msg", "Location access granted")
        } else {
            Log.d("msg", "Location access denied")
        }
    }

// LaunchedEffect 用于在启动时检查并请求多个权限
    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }


    // 获取当前页面的生命周期
    val lifecycleOwner = LocalLifecycleOwner.current

    // 使用 DisposableEffect 监听生命周期
    DisposableEffect(lifecycleOwner) {
        // 创建生命周期观察者
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // 当页面重新可见时，重新加载数据
                viewModel.loadTeacherClasses()
            }
        }

        // 监听生命周期
        lifecycleOwner.lifecycle.addObserver(observer)

        // 当 Composable 销毁时移除观察者
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var query by remember { mutableStateOf("") }

    ModalNavigationDrawer(drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController=navController)
        }
    ) {
        Scaffold(floatingActionButton = {
            AddClassesFloatingActionBar(onClick = { navController.navigate(StuRoute.ADDCLASSSCAN.name) })
        },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                BottomBar(
                    content = "😀 Hello,${currentUser?.username}",
                )
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            topBar = {StuSearchBar(
                query = query,
                onQueryChange = { query = it },
            )}) {
            StuClassesContent(
                classes = classes,
                getTeaName = { teacherId -> viewModel.getTeacherName(teacherId) },
                onClick = { classObjId -> navController.navigate(StuRoute.ATTENDANCE2.name + "/$classObjId") },
                modifier = Modifier.padding(it),
                query=query

            )
        }
    }
}


@Composable
fun AddClassesFloatingActionBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(onClick = onClick, modifier = modifier) {
        Icon(Qr_code_scanner, contentDescription = null)
    }
}

@Composable
fun StuClassesContent(
    classes: List<Class>?,
    getTeaName:suspend (String) -> String?,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    query:String
) {
    Column(modifier = modifier) {
        if (classes != null) {

            // 根据搜索关键词过滤班级列表
            val filteredClasses = classes.filter { it.className.contains(query, ignoreCase = true) }

            if (filteredClasses.isNotEmpty()) {
                LazyColumn (modifier = Modifier.
                background(color = MaterialTheme.colorScheme.primaryContainer)){
                    itemsIndexed(filteredClasses) { _, item ->
                        var teacherName by remember { mutableStateOf<String?>(null) }

                        LaunchedEffect(item.teacherId) {
                            teacherName = getTeaName(item.teacherId)
                        }

                        ClassInfoCard(
                            teacherName = teacherName,
                            cLass = item
                        ) {
                            onClick(item.objectId)
                        }
                    }
                }
            }
            else{
                Column(
                    modifier = modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nature_people_24px),
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )

                    Text(
                        text = "No Such a Class\n 😀",
                        style = MaterialTheme.typography.titleLarge,  // 修改为更大的字体样式
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp,
                        lineHeight = 40.sp,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StuSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
) {


    var active by remember { mutableStateOf(false) }
    SearchBar(
        modifier = modifier,
        query = query,
        onQueryChange = { onQueryChange(it)},
        onSearch = {
            // 执行搜索操作
            active=false
        },
        active = active,
        onActiveChange = { active = it },
        placeholder = {
            Text(
                "Search Class name ",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondary
            )
        },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        colors =SearchBarDefaults.colors(dividerColor = MaterialTheme.colorScheme.outline,
            containerColor = MaterialTheme.colorScheme.inversePrimary
        )
    ) {

    }
}
