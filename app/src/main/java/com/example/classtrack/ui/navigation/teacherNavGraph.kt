package com.example.classtrack.ui.navigation

import android.os.Build
import android.util.Log
import androidx.collection.emptyLongSet
import androidx.compose.runtime.internal.composableLambda
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.classtrack.data.Class
import com.example.classtrack.ui.screens.AddStuScreen
import com.example.classtrack.ui.screens.TeaAttendanceDetailScreen
import com.example.classtrack.ui.screens.TeaClassDetailScreen
import com.example.classtrack.ui.screens.TeaClassesScreen
import com.example.classtrack.ui.screens.TeaPassCodeScreen
import com.example.classtrack.ui.screens.TeaQRScreen
import com.example.classtrack.ui.screens.TestScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

enum class TeaRoute {
    CLASS,
    ATTENDANCE,
    ADDSTUDENT,
    QR,
    PASSCODE,
    ATTENDANCEDTEAIL
}

fun NavGraphBuilder.teacherNavGraph(navController: NavController) {
    navigation(startDestination = TeaRoute.CLASS.name, route = Route.TEACHER.name)
    {
        composable(route = TeaRoute.CLASS.name) {
            TeaClassesScreen(navController = navController)
        }

        composable(
            route = TeaRoute.ATTENDANCE.name + "/{classObjId}",
            arguments = listOf(navArgument("classObjId") { type = NavType.StringType })
        ) {
            val classObjId = it.arguments?.getString("classObjId") ?: ""
            Log.d("detail", classObjId)
            TeaClassDetailScreen(navController = navController, cLassObjId = classObjId)
        }

        composable(
            route = TeaRoute.ADDSTUDENT.name + "/{classObjId}",
            arguments = listOf(navArgument("classObjId") { type = NavType.StringType })
        ) {
            val classObjId = it.arguments?.getString("classObjId") ?: ""
            AddStuScreen(navController = navController, cLassObjId = classObjId)
        }

        composable(
            route = TeaRoute.QR.name + "/{classId}",
            arguments = listOf(navArgument("classId") {
                type = NavType.StringType
            })
        ) {
            val classId = it.arguments?.getString("classId") ?: ""
            TeaQRScreen(navController = navController, classId = classId)
        }

        composable(
            route = TeaRoute.PASSCODE.name + "/{classObj}/{passcode}",
            arguments = listOf(
                navArgument("passcode") { type = NavType.StringType },
                navArgument("classObj") { type = NavType.StringType },
            )
        ) {
            val passCode = it.arguments?.getString("passcode") ?: ""
            val classObj = it.arguments?.getString("classObj") ?: ""
            TeaPassCodeScreen(
                navController = navController,
                passCode = passCode,
                classId = classObj
            )
        }

        composable(
            TeaRoute.ATTENDANCEDTEAIL.name+ "/{classId}/{attendanceId}",
            arguments = listOf(
                navArgument("classId") { type = NavType.StringType },
                navArgument("attendanceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val classId = backStackEntry.arguments?.getString("classId")
            val attendanceId = backStackEntry.arguments?.getString("attendanceId")
            if (classId != null && attendanceId != null) {
                TeaAttendanceDetailScreen(
                    navController = navController,
                    classId = classId, attendanceId = attendanceId
                )
            }
        }
    }
}
