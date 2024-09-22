package com.example.classtrack.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.classtrack.R
import com.example.classtrack.data.Class

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStuScreen(cLassObjId:String,
                 navController: NavController,
                 viewModel: AddStuScreenViewModel= hiltViewModel()){
    val context= LocalContext.current
    viewModel.fetchClassById(classId = cLassObjId)
    val cLass by viewModel.classLiveData.observeAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Scan the QR code and Join my Class") },
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}) {
                        Icon(
                            painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Return"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val img = viewModel.generateClassQRCode(classId = cLass?.objectId ?: "")

                if (img != null) {
                    // 展示二维码图片
                    Image(
                        bitmap = img.asImageBitmap(),
                        contentDescription = "Scan the QR code and Join My Class",
                        modifier = Modifier
                            .size(250.dp)
                            .padding(16.dp)
                    )
                }

                // 班级名称
                Text(
                    text = cLass?.className ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(8.dp)
                )

                // 提示说明
                Text(
                    text = "Scan QR code above,and join My Class",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp),
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 操作按钮：保存二维码或分享
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = {
                        img?.let {
                            val uri=viewModel.saveQRCodeToGallery(context =context,it )
                            Toast.makeText(context, "二维码已保存到相册", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            painterResource(id = R.drawable.baseline_save_24),
                            contentDescription = "保存二维码"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Save the QR code ")
                    }

                    Button(onClick = {
                        img?.let {
                            viewModel.shareQRCode(context, it)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share the QR code"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Share the QR code ")
                    }
                }
            }
        }
    }
}