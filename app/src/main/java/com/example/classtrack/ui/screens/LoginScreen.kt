package com.example.classtrack.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.classtrack.R
import com.example.classtrack.data.UserType
import com.example.classtrack.ui.component.LoadingAnimation
import com.example.classtrack.ui.navigation.AuthRoute
import com.example.classtrack.ui.navigation.Route
import com.example.classtrack.ui.theme.ClassTrackTheme
import com.example.classtrack.ui.theme.RedVisne
import com.example.classtrack.ui.util.loginCheckValid
import com.example.classtrack.ui.util.loginIn
import kotlinx.coroutines.launch



@Composable
fun LoginScreen(navController: NavController,
                viewModel: LoginViewModel=hiltViewModel(),
                userName:String="NULL") {

    Log.d("Navigation stack","Here is Login Screen")

    val loginState by viewModel.loginState.collectAsState()
    val userType=viewModel.userType

    val context = LocalContext.current

    val intent = remember {
        Intent(Settings.ACTION_WIRELESS_SETTINGS)
    }

    val scope = rememberCoroutineScope()

    val username = remember {
        mutableStateOf("")
    }

    if(userName!="NULL"){
        username.value=userName
    }


    val password = remember {
        mutableStateOf("")
    }

    val keyboardController = LocalSoftwareKeyboardController.current


    var passwordVisibility by remember {
        mutableStateOf(false)
    }

    val isErrorUsername = remember {
        mutableStateOf(false)
    }

    val errorUsernameMessage = remember {
        mutableStateOf("NULL")
    }

    val errorPasswordMessage = remember {
        mutableStateOf("NULL")
    }


    val icon = if (passwordVisibility)
        painterResource(id = R.drawable.ic_visibility)
    else
        painterResource(id = R.drawable.ic_visibility_off)

    val snackbarHostState = remember { SnackbarHostState() }

    var isNavigated by remember { mutableStateOf(false) }

    if(loginState==LoginState.Success && !isNavigated)
    {
        isNavigated=true
        navController.navigate(if(userType.value=="TEACHER") Route.TEACHER.name else Route.STUDENT.name)
        {
            popUpTo(AuthRoute.LOGIN.name){
                inclusive=true
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState)},
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        it->
        Box(contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            Column {
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxWidth()
                ) {
                    Image(modifier =Modifier.fillMaxWidth() ,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = R.drawable.greet))

                }
                Column(
                    modifier = Modifier
                        .weight(2.7f)
                        .fillMaxWidth()
                        .offset(y = (-30).dp)
                        .background(
                            color = Color.White,
                            RoundedCornerShape(
                                topStart = 30.dp,
                                topEnd = 30.dp
                            )
                        )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center, modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp)
                    ) {

                        Text(
                            text = stringResource(R.string.welcome),
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 29.sp,
                            fontWeight = FontWeight.Bold
                        )

                    }
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxSize()
                    ) {

                        Column(modifier = Modifier) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 21.dp,
                                        end = 21.dp,
                                        top = 24.dp
                                    ),
                                value = username.value,
                                onValueChange = { username.value = it },
                                label = {
                                    Text(
                                        text = stringResource(R.string.username),
                                        color = Color.Black
                                    )
                                },
                                leadingIcon = {

                                    IconButton(onClick = {}) {

                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = "AccountCircle İcon"
                                        )

                                    }
                                },

                                keyboardOptions = KeyboardOptions(

                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),

                                trailingIcon = {

                                    if (isErrorUsername.value)
                                        Icon(
                                            Icons.Filled.Warning,
                                            contentDescription = "E-Mail Error Icon",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                }
                            )

                            if (isErrorUsername.value) {
                                Text(
                                    text = errorUsernameMessage.value,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(
                                        top = 6.dp,
                                        start = 21.dp
                                    )
                                )
                            }
                            
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 21.dp,
                                        end = 21.dp,
                                        top = 12.dp
                                    ),
                                value = password.value,
                                onValueChange = { password.value = it },
                                label = {
                                    Text(
                                        text = stringResource(id =R.string.password ),
                                        color = Color.Black
                                    )
                                },
                                leadingIcon = {

                                    IconButton(onClick = {}) {

                                        Icon(
                                            imageVector = Icons.Filled.Lock,
                                            contentDescription = "Password İcon"
                                        )

                                    }
                                },

                                trailingIcon = {

                                    IconButton(onClick = {

                                        passwordVisibility = !passwordVisibility

                                    }) {

                                        Icon(
                                            painter = icon,
                                            contentDescription = "Password İcon"
                                        )

                                    }

                                },

                                visualTransformation = if (passwordVisibility) VisualTransformation.None
                                else PasswordVisualTransformation(),

                                singleLine = true,

                                keyboardOptions = KeyboardOptions(

                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done,
                                ),

                                keyboardActions = KeyboardActions(

                                    onDone = {

                                        keyboardController?.hide()

                                    }
                                ),
                            )

                            if (errorPasswordMessage.value != "NULL") {
                                Text(
                                    text = errorPasswordMessage.value,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(
                                        top = 6.dp,
                                        start = 21.dp
                                    )
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.End, modifier = Modifier
                                    .padding(
                                        top = 9.dp,
                                        end = 21.dp
                                    )
                                    .fillMaxWidth()
                            ){}

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 21.dp * 2,
                                        end = 21.dp * 2,
                                        top = 9.dp * 2
                                    ),
                                horizontalArrangement = Arrangement.Center
                            ) {

                                Button(
                                    onClick = {
                                        when(loginCheckValid(username.value,password.value)){
                                            1->{
                                                isErrorUsername.value=true
                                                errorUsernameMessage.value=
                                                    context.getString(R.string.enter_your_username)
                                                
                                            }
                                            2->{
                                                isErrorUsername.value=false
                                                errorUsernameMessage.value="NULL"
                                                errorPasswordMessage.value=
                                                    context.getString(R.string.enter_your_password)
                                            }
                                            3->{
                                                isErrorUsername.value=false
                                                errorUsernameMessage.value="NULL"
                                                errorPasswordMessage.value="NULL"
                                                viewModel.login(username=username.value,
                                                    password=password.value,
                                                    snackbarHostState=snackbarHostState,
                                                    scope=scope,
                                                    context=context)
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(24.dp),
                                    modifier = Modifier
                                        .fillMaxWidth(),

                                    colors = ButtonDefaults.buttonColors(
                                        contentColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        containerColor =MaterialTheme.colorScheme.tertiary
                                    )

                                ) {

                                    Text(

                                        text = stringResource(id = R.string.login),
                                        fontSize = 18.sp,
                                        modifier = Modifier
                                            .padding(
                                                top = 6.dp,
                                                bottom = 6.dp
                                            )

                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                if (loginState==LoginState.Loading) {

                                    LoadingAnimation(speed = 4f)

                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(text = stringResource(R.string.no_account))

                            Spacer(modifier = Modifier.padding(3.dp))

                            Text(
                                text = stringResource(R.string.sign_up),
                                color = RedVisne,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable {
                                        navController.navigate(AuthRoute.SIGNUP.name){
                                            popUpTo(AuthRoute.SIGNUP.name){inclusive=false}
                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(){
    ClassTrackTheme {
        val navController = rememberNavController()
        LoginScreen(navController)
    }
}