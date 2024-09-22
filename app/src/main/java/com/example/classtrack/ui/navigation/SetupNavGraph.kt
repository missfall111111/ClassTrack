package com.example.classtrack.ui.navigation

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import cn.bmob.v3.BmobUser
import com.example.classtrack.data.Class
import com.example.classtrack.data.User
import com.example.classtrack.data.UserType
import com.example.classtrack.ui.screens.TeaClassesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

enum class Route {
    ROOT,
    AUTH,
    STUDENT,
    TEACHER,
}


@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Route.AUTH.name,
        route = Route.ROOT.name
    ) {
        authNavGraph(navController = navController)
        teacherNavGraph(navController = navController)
        studentNavGraph(navController = navController)
    }
}


