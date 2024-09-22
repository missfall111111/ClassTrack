package com.example.classtrack.ui.screens

import android.util.Log
import android.window.SplashScreen
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.classtrack.ui.component.HaznedarText
import com.example.classtrack.ui.component.LoadingAnimation
import com.example.classtrack.ui.component.WelcomeLogo
import com.example.classtrack.ui.navigation.AuthRoute
import com.example.classtrack.ui.navigation.Route
import com.example.classtrack.ui.theme.RedVisne
import com.example.classtrack.ui.util.Sp
import com.example.classtrack.ui.util.getSharedPreference

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    Log.d("Navigation stack","Here is Flash Screen")

    val splashState by viewModel.splashState.collectAsState()
    val userType = viewModel.userType

    val context = LocalContext.current

    val username = getSharedPreference(context, Sp.USERNAME, "NULL").toString()

    val password = getSharedPreference(context, Sp.PASSWORD, "NULL").toString()

    LaunchedEffect(key1 = Unit) {

        viewModel.login(username = username, password = password)
//        Log.d("Splash","Login 函数启动")
    }

    var isNavigated by remember { mutableStateOf(false) }

    if (splashState == SplashState.Success && !isNavigated) {
        isNavigated = true
        navController.navigate(if (userType.value == "TEACHER") Route.TEACHER.name else Route.STUDENT.name) {
            popUpTo(route = AuthRoute.SPLASH.name) { inclusive = true }
        }
    }

    if (splashState == SplashState.Failed && !isNavigated) {
        isNavigated = true
        navController.navigate(route = AuthRoute.LOGIN.name) {
            popUpTo(route = AuthRoute.SPLASH.name) { inclusive = true }
        }
    }

    Scaffold (containerColor = MaterialTheme.colorScheme.primary){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Spacer(modifier = Modifier.height(200.dp))
            WelcomeLogo()

            if (splashState == SplashState.Loading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground)
            }
            else{
                Spacer(modifier = Modifier.height(40.dp))
            }
            
            
            Spacer(modifier = Modifier.height(100.dp))
            HaznedarText(modifier = Modifier.align(Alignment.CenterHorizontally))

        }
    }

}

@Preview
@Composable
fun SplashScreenPreview() {
    val navController = rememberNavController()
    SplashScreen(navController = navController)
}
