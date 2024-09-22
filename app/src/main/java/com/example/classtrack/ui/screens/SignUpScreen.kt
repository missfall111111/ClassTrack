package com.example.classtrack.ui.screens

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.classtrack.R
import com.example.classtrack.data.UserType
import com.example.classtrack.ui.component.FancyIndicatorContainerTabs
import com.example.classtrack.ui.component.LoadingAnimation
import com.example.classtrack.ui.navigation.AuthRoute
import com.example.classtrack.ui.theme.ClassTrackTheme
import com.example.classtrack.ui.theme.RedVisne
import com.example.classtrack.ui.theme.provider
import com.example.classtrack.ui.util.registerCheckValid

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {

    Log.d("Navigation stack","Here is SignUp Screen")

    val signUpState by viewModel.signUpState.collectAsState()
//    val scaffoldState = rememberScaffoldState()

    val scope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current

//    val state = viewModel.state.value

    val context = LocalContext.current

    val username = remember {
        mutableStateOf("")
    }

//    if (email != "Null") {
//
//        LaunchedEffect(key1 = Unit) {
//
//            username.value = email
//
//        }
//    }

//    val checkboxDurum = remember {
//        mutableStateOf(true)
//    }

    val passwordOne = remember {
        mutableStateOf("")
    }

    val passwordTwo = remember {
        mutableStateOf("")
    }

    val userType = remember {
        mutableStateOf(UserType.STUDENT)
    }

    val userTypes = listOf(UserType.STUDENT.name, UserType.TEACHER.name)

    val selectedIndex = userTypes.indexOf(userType.value.name)

    val isErrorUsername = remember {
        mutableStateOf(false)
    }

    val errorUsernameMessage = remember {
        mutableStateOf("NULL")
    }

    val errorPasswordMessage = remember {
        mutableStateOf("NULL")
    }

    var passwordVisibility by remember {
        mutableStateOf(false)
    }

    var passwordVisibilityTwo by remember {
        mutableStateOf(false)
    }

    val icon = if (passwordVisibility)
        painterResource(id = R.drawable.ic_visibility)
    else
        painterResource(id = R.drawable.ic_visibility_off)

    val iconTwo = if (passwordVisibilityTwo)
        painterResource(id = R.drawable.ic_visibility)
    else
        painterResource(id = R.drawable.ic_visibility_off)



    if (signUpState == SignUpState.Success) {
        AlertDialog(
            onDismissRequest = { viewModel.dismiss() },
            title = { Text(text = stringResource(R.string.congratulations)) },
            text = { Text(text = "Your registration has been completed successfully.You can login in") },
            confirmButton = {
                TextButton(
                    onClick = {
                        navController.navigate(AuthRoute.LOGIN.name) {
                            popUpTo(route = AuthRoute.LOGIN.name) {
                                inclusive = true
                            }
                        }

                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismiss() }
                ) {
                    Text(stringResource(R.string.dismiss))
                }
            },
            icon = { Icon(imageVector = Icons.Rounded.ThumbUp, contentDescription = null) }

        )
    }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.inversePrimary
    ) { contentPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column() {
                Column(modifier = Modifier.weight(1.3f)) {

                    Image(

                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        painter = painterResource(id = R.drawable.greet)
                    )


                }


                Column(
                    modifier = Modifier
                        .weight(2.7f)
                        .fillMaxWidth()
                        .offset(y = (-30).dp)
                        .background(
                            color = Color.White,
                            RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                        )

                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center, modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp)
                    ) {

                        Text(
                            text = stringResource(R.string.sign_up),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                    }
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Column {

                            FancyIndicatorContainerTabs(Titles = userTypes,
                                modifier = Modifier.padding(
                                    top = 20.dp,
                                    start = 20.dp,
                                    end = 20.dp
                                ),
                                selectedIndex = selectedIndex,
                                onTabSelected = { index ->
                                    userType.value =
                                        if (index == 0) UserType.STUDENT else UserType.TEACHER
                                })

                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, end = 20.dp, top = 15.dp),
                                value = username.value,
                                onValueChange = { username.value = it },
                                label = {
                                    Text(
                                        text = stringResource(R.string.username),
                                        color = Color.Black
                                    )
                                },

                                leadingIcon = {

                                    IconButton(onClick = {

                                    }) {

                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = "E-Mail İcon"
                                        )

                                    }
                                },

                                trailingIcon = {

                                    if (isErrorUsername.value)
                                        Icon(
                                            Icons.Filled.Warning,
                                            contentDescription = "E-Mail Error Icon",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                },

                                keyboardOptions = KeyboardOptions(

                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next

                                )

                            )

                            if (isErrorUsername.value) {
                                Text(
                                    text = errorUsernameMessage.value,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(top = 5.dp, start = 20.dp)
                                )
                            }

                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, end = 20.dp, top = 5.dp),
                                value = passwordOne.value,
                                onValueChange = { passwordOne.value = it },
                                label = {
                                    Text(
                                        text = stringResource(R.string.password),
                                        color = Color.Black
                                    )
                                },

                                leadingIcon = {
                                    IconButton(onClick = {


                                    }) {

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

                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next

                                )

                            )

                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp, end = 20.dp, top = 5.dp),
                                value = passwordTwo.value,
                                onValueChange = { passwordTwo.value = it },
                                label = {
                                    Text(
                                        text = context.getString(R.string.confirm_password),
                                        color = Color.Black
                                    )
                                },

                                leadingIcon = {
                                    IconButton(onClick = {


                                    }) {

                                        Icon(
                                            imageVector = Icons.Filled.Lock,
                                            contentDescription = "Password İcon"
                                        )

                                    }
                                },

                                trailingIcon = {

                                    IconButton(onClick = {

                                        passwordVisibilityTwo = !passwordVisibilityTwo

                                    }) {
                                        Icon(
                                            painter = iconTwo,
                                            contentDescription = "Password İcon"
                                        )
                                    }

                                },

                                visualTransformation = if (passwordVisibilityTwo) VisualTransformation.None
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

                                )

                            )

                            if (errorPasswordMessage.value != "NULL") {
                                Text(
                                    text = errorPasswordMessage.value,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(top = 5.dp, start = 20.dp)
                                )
                            }

                            Row(
                                modifier = Modifier.padding(start = 20.dp, top = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 50.dp, end = 50.dp, top = 30.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = {
                                        when (registerCheckValid(
                                            userName = username.value,
                                            password1 = passwordOne.value,
                                            password2 = passwordTwo.value
                                        )) {
                                            1 -> {
                                                isErrorUsername.value = true
                                                errorUsernameMessage.value =
                                                    context.getString(R.string.enter_your_username)
                                                errorPasswordMessage.value = "NULL"
                                            }

                                            2 -> {
                                                isErrorUsername.value = false
                                                errorPasswordMessage.value =
                                                    context.getString(R.string.enter_your_password)
                                            }

                                            3 -> {
                                                isErrorUsername.value = false
                                                errorPasswordMessage.value =
                                                    context.getString(R.string.passwords_do_not_match)
                                            }

                                            4 -> {
                                                isErrorUsername.value = false
                                                errorPasswordMessage.value = "NULL"
                                                viewModel.signUp(
                                                    username = username.value,
                                                    password = passwordTwo.value,
                                                    userType = userType.value,
                                                    scope = scope,
                                                    context = context,
                                                    snackbarHostState = snackbarHostState
                                                )
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(25.dp),
                                    modifier = Modifier
                                        .fillMaxWidth(),

                                    colors = ButtonDefaults.buttonColors(
                                        contentColor = MaterialTheme.colorScheme.primary,
                                        containerColor =MaterialTheme.colorScheme.primaryContainer ,
                                    )

                                ) {
                                    Text(

                                        text = stringResource(id = R.string.sign_up),
                                        fontSize = 18.sp,
                                        modifier = Modifier
                                            .padding(top = 5.dp, bottom = 5.dp)

                                    )
                                }
                            }

                            if (signUpState == SignUpState.Loading) {

                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 3.dp)
                                ) {

                                    LoadingAnimation(speed = 3.75f)

                                }

                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = stringResource(R.string.have_account))
                            Spacer(modifier = Modifier.padding(3.dp))
                            Text(
                                text = stringResource(id = R.string.login),
                                color = RedVisne,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    navController.navigate(AuthRoute.LOGIN.name) {
                                        popUpTo(AuthRoute.LOGIN.name) { inclusive = true }
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

@Preview
@Composable
fun SignUpScreenPreview() {
//    ClassTrackTheme {
//        val navController = rememberNavController()
//        SignUpScreen(navController = navController)
//    }
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        title = { Text(text = stringResource(R.string.congratulations)) },
        text = { Text(text = "Your registration has been completed successfully.You can login in") },
        confirmButton = {
            TextButton(
                onClick = {
//                    navController.navigate(AuthRoute.LOGIN.name){
//                        popUpTo(route = AuthRoute.SIGNUP.name){
//                            inclusive=true
//                        }
//                    }
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
//        dismissButton = {
//            TextButton(
//                onClick = {
//                }
//            ) {
//                Text(stringResource(R.string.dismiss))
//            }
//        },
        icon = { Icon(imageVector = Icons.Rounded.ThumbUp, contentDescription = null) }

    )
}