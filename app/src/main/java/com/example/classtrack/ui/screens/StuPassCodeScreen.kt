package com.example.classtrack.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun StuPassCodeScreen(navController: NavController,
                      attendanceId:String,
                      viewModel: StuPassCodeVIewModel= hiltViewModel()) {
    val context= LocalContext.current
    val passcodeFields = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = List(6) { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val snackbarHostState = remember { SnackbarHostState() }
    val isPasscodeValid by viewModel.isPasscodeValid.collectAsState()

    if (isPasscodeValid) {
        navController.popBackStack()
    }


    val isPasscodeIncorrect by viewModel.isPasscodeIncorrect.collectAsState()
    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
        keyboardController?.show()
    }

    val scope= rememberCoroutineScope()
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState,
                snackbar = { data ->
                    CustomSnackbar(snackbarData = data) // 自定义 Snackbar 样式
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp).offset(y= (-600).dp))

        },
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LaunchedEffect(isPasscodeIncorrect) {
                if (isPasscodeIncorrect) {

                    // 清空所有输入框
                    passcodeFields.fill("")
                    focusRequesters[0].requestFocus() // 重置焦点到第一个输入框
                    scope.launch {
                        snackbarHostState.showSnackbar("Incorrect passcode, please try again.")
                    }
                    viewModel.resetPasscodeIncorrect() // 确保状态在处理完后重置

                }
            }

            Text(
                text = "Enter your Passcode",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (i in passcodeFields.indices) {
                    TextField(
                        value = passcodeFields[i],
                        onValueChange = { newValue ->
                            // 输入逻辑：如果当前框为空，把字符填写到当前框，跳到下一个框
                            if (newValue.length == 1) {
                                if (passcodeFields[i].isEmpty()) {
                                    passcodeFields[i] = newValue
                                    if (i < passcodeFields.size - 1) {
                                        focusRequesters[i + 1].requestFocus()
                                    } else {
                                        val passcode = passcodeFields.joinToString("")
                                        // 隐藏键盘
                                        keyboardController?.hide()
                                        viewModel.submitPasscode(
                                            inputPasscode = passcode,
                                            attendanceObjectId = attendanceId,
                                            context = context
                                        )
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(1.dp)
                            .size(72.dp)
                            .focusRequester(focusRequesters[i])
                            .onKeyEvent { keyEvent ->
                                // 删除逻辑：如果当前框为空并且按下删除键，删除前一个框内容并移动到前一个框
                                if (keyEvent.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DEL) {
                                    if (passcodeFields[i].isEmpty() && i > 0) {
                                        passcodeFields[i - 1] = ""
                                        focusRequesters[i - 1].requestFocus()
                                    }
                                    true
                                } else {
                                    false
                                }
                            },
                        textStyle = MaterialTheme.typography.headlineSmall,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = if (i == passcodeFields.size - 1) ImeAction.Done else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                if (i < passcodeFields.size - 1) {
                                    focusRequesters[i + 1].requestFocus()
                                }
                            },
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        colors = TextFieldColors(
                            focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                            disabledTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                            errorTextColor = MaterialTheme.colorScheme.error,

                            focusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                            errorContainerColor = MaterialTheme.colorScheme.errorContainer,

                            cursorColor = MaterialTheme.colorScheme.primary,
                            errorCursorColor = MaterialTheme.colorScheme.error,

                            textSelectionColors = TextSelectionColors(
                                handleColor = MaterialTheme.colorScheme.primary,
                                backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            ),

                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                            disabledIndicatorColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f),
                            errorIndicatorColor = MaterialTheme.colorScheme.error,

                            focusedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                            disabledLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                            errorLeadingIconColor = MaterialTheme.colorScheme.error,

                            focusedTrailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                            errorTrailingIconColor = MaterialTheme.colorScheme.error,

                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                            disabledLabelColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                            errorLabelColor = MaterialTheme.colorScheme.error,

                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f),
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f),
                            errorPlaceholderColor = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),

                            focusedSupportingTextColor = MaterialTheme.colorScheme.primary,
                            unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                            disabledSupportingTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                            errorSupportingTextColor = MaterialTheme.colorScheme.error,

                            focusedPrefixColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unfocusedPrefixColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                            disabledPrefixColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                            errorPrefixColor = MaterialTheme.colorScheme.error,

                            focusedSuffixColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unfocusedSuffixColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                            disabledSuffixColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                            errorSuffixColor = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CustomSnackbar(snackbarData: SnackbarData) {
    Snackbar(
        modifier = Modifier, // 你可以根据需求调整此处的 Modifier
        shape = RoundedCornerShape(8.dp), // 设置圆角
        containerColor = MaterialTheme.colorScheme.errorContainer, // 设置背景颜色
        contentColor = MaterialTheme.colorScheme.error// 设置文字颜色
    ) {
        Text(
            text = snackbarData.visuals.message,
            fontSize = 16.sp
        )
    }
}







