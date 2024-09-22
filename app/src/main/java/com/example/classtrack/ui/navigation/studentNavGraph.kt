package com.example.classtrack.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.classtrack.ui.screens.StuAddClassQRCodeScannerScreen
import com.example.classtrack.ui.screens.StuAttendanceScreen
import com.example.classtrack.ui.screens.StuClassesScreen
import com.example.classtrack.ui.screens.StuPassCodeScreen
import com.example.classtrack.ui.screens.StuScanAttendanceScreen
import com.example.classtrack.ui.screens.TestScreen

enum class StuRoute{
    CLASSES,
    ADDCLASSSCAN,
    ATTENDANCE2,
    SCANATTENDANCE,
    PASSCODE
}
fun NavGraphBuilder.studentNavGraph(navController: NavController){
    navigation(startDestination =StuRoute.CLASSES.name,route=Route.STUDENT.name )
    {
        composable(route = StuRoute.CLASSES.name){
            StuClassesScreen(navController = navController)
        }

        composable(route=StuRoute.ADDCLASSSCAN.name){
            StuAddClassQRCodeScannerScreen(navController = navController)
        }

        composable(route=StuRoute.ATTENDANCE2.name+"/{classObjId}",
            arguments = listOf(navArgument("classObjId"){
                type= NavType.StringType
            })
        ){
            val classObjId=it.arguments?.getString("classObjId")?:""
            StuAttendanceScreen(navController=navController, classObj = classObjId)
        }

        composable(route=StuRoute.SCANATTENDANCE.name){
            StuScanAttendanceScreen(navController = navController)
        }

        composable(route=StuRoute.PASSCODE.name+"/{attendanceId}",
            arguments = listOf(navArgument("attendanceId"){
                type=NavType.StringType
            })
        ){
            val attendanceId=it.arguments?.getString("attendanceId")?:""
            StuPassCodeScreen(navController,attendanceId=attendanceId)
        }

    }
}