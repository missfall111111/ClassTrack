package com.example.classtrack.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.classtrack.ui.screens.InfoScreen
import com.example.classtrack.ui.screens.LoginScreen
import com.example.classtrack.ui.screens.SignUpScreen
import com.example.classtrack.ui.screens.SplashScreen

enum class AuthRoute{
    LOGIN,
    SIGNUP,
    SPLASH,
    INFO,
}
fun NavGraphBuilder.authNavGraph(navController: NavController) {
    navigation(startDestination = AuthRoute.SPLASH.name, route = Route.AUTH.name){

        composable(route=AuthRoute.SPLASH.name){
            SplashScreen(navController = navController)
        }


        composable(route = AuthRoute.LOGIN.name){
            LoginScreen(navController)
        }

        composable(route = AuthRoute.LOGIN.name+"/{userName}",
            arguments = listOf(navArgument("userName") { type = NavType.StringType })
        ){
            val userName = it.arguments?.getString("userName") ?: "NULL"
            LoginScreen(navController,userName=userName)
        }


        composable(route=AuthRoute.SIGNUP.name){
            SignUpScreen(navController = navController)
        }

        composable(route=AuthRoute.INFO.name){
            InfoScreen(navController = navController)
        }
    }
}