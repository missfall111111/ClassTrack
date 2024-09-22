package com.example.classtrack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController


@Composable
fun TeaQRScreen(navController: NavController,
                classId:String,
                viewModel: TeaQRViewModel= hiltViewModel()){

    val qrCode by viewModel.qrCodeBitmap.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.startGeneratingAttendanceQrCode(classId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopGeneratingQrCode()
            viewModel.endAttendanceByClassId(classId=classId)
        }
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            qrCode?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Attendance QR Code",
                    modifier = Modifier.size(300.dp)
                )
            }
            Text(
                text = "Scan this QR code to mark your attendance",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}