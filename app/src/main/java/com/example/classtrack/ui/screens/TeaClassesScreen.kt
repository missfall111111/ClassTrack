package com.example.classtrack.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import cn.bmob.v3.BmobUser
import com.example.classtrack.R
import com.example.classtrack.data.Class
import com.example.classtrack.ui.component.BottomBar
import com.example.classtrack.ui.component.DrawerContent
import com.example.classtrack.ui.component.People
import com.example.classtrack.ui.component.School
import com.example.classtrack.ui.navigation.TeaRoute


@Composable
fun TeaClassesScreen(navController: NavController,
                     viewModel: TeaClassesViewModel= hiltViewModel()) {
    val currentUser: BmobUser? = BmobUser.getCurrentUser(BmobUser::class.java)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    ModalNavigationDrawer(drawerState = drawerState,
        drawerContent = {
            DrawerContent(navController=navController)
        }
    ) {
        var query by remember { mutableStateOf("") }

        var showDialog by remember { mutableStateOf(false) }
        Scaffold(floatingActionButton = { NewClassActionBar(onClick = {showDialog=true},
            modifier = Modifier
                .offset(y = (-80).dp)) },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = { TeaSearchBar(
                query=query,onQueryChange = { query = it })},
            bottomBar = { BottomBar(content = "😀 Hello,${currentUser?.username}")},
            containerColor = MaterialTheme.colorScheme.primaryContainer,)
        { padding ->
            TeaClassesContent(modifier = Modifier.padding(padding),
                showDialog=showDialog,
                onDismissRequest = {showDialog=false},
                onConfirmRequest = { showDialog = false },
                viewModel=viewModel,
                snackbarHostState = snackbarHostState,
                navController = navController,
                query=query)
        }
    }
}


@Composable
fun TeaClassesContent(modifier: Modifier = Modifier,
                     showDialog:Boolean,
                     onDismissRequest:()->Unit,
                     onConfirmRequest:()->Unit,
                     viewModel: TeaClassesViewModel,
                     snackbarHostState: SnackbarHostState,
                      navController: NavController,
                      query: String) {
        val classes by viewModel.classes.collectAsState()
        
        val currentUser: BmobUser? = BmobUser.getCurrentUser(BmobUser::class.java)
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        var className by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }
        if(showDialog){
            AlertDialog(
                onDismissRequest = onDismissRequest,
                title = { Text("Create New Class") },
                text = {
                    Column {
                        TextField(
                            value = className,
                            onValueChange = { className = it },
                            label = { Text("Class Name") },
                            modifier = Modifier.fillMaxWidth(),

                            keyboardOptions = KeyboardOptions(

                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done

                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (className.isNotEmpty() ) {
                                errorMessage = ""
                                onConfirmRequest()
                                if (currentUser != null) {
                                    viewModel.newClass(className=className,
                                        context = context,
                                        teaId = currentUser.objectId,
                                        scope = scope,
                                        snackbarHostState = snackbarHostState)
                                }
                                className=""
                            } else {
                                errorMessage = "Please fill in all fields."
                            }
                        }
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                },
                icon ={ Icon(School, contentDescription = null)}
            )
        }
        Column (modifier = modifier){
            val filteredClasses = classes?.filter { it.className.contains(query, ignoreCase = true) }

            if(classes!=null){
                if (filteredClasses?.isNotEmpty() == true) {
                    LazyColumn() {
                        itemsIndexed(filteredClasses) {
                                _, item ->
                            ClassInfoCard(teacherName = currentUser?.username ?: "",
                                cLass = item) {
//                            Log.d("detail","${item.objectId}" )
                                navController.navigate(TeaRoute.ATTENDANCE.name+"/${item.objectId}")
//                            navController.navigate(TeaRoute.TEST.name)
                            }
                        }
                    }
                }
                else
                {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.nature_people_24px),
                            contentDescription = null,
                            modifier = Modifier.size(200.dp)
                        )

                        Text(
                            text = "No Such a Class\n 😀",
                            style = MaterialTheme.typography.titleLarge,  // 修改为更大的字体样式
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            lineHeight = 40.sp,
                        )
                    }
                }
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeaSearchBar(modifier: Modifier=Modifier,
                 query: String,
                 onQueryChange: (String) -> Unit, ) {

    var active by remember { mutableStateOf(false) }
    SearchBar(
        modifier = modifier,
        query = query,
        onQueryChange = { onQueryChange(it) },
        onSearch = {
            // 执行搜索操作
            active=false
        },
        active = active,
        onActiveChange = { active = it },
        placeholder = {
            Text(
                "Search Class name ",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondary
            )
        },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        colors = SearchBarDefaults.colors(dividerColor = MaterialTheme.colorScheme.outline,
            containerColor = MaterialTheme.colorScheme.inversePrimary
        )
    ) {

    }
}

//@Preview
@Composable
fun NewClassActionBar(modifier: Modifier = Modifier,
                      onClick: ()->Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier =Modifier.size(40.dp) )
    }
}


@Composable
fun ClassInfoCard(
    teacherName: String?,
    cLass:Class,
    modifier: Modifier = Modifier,
    onDetailClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Box{
            Image(
                painter = painterResource(id = R.drawable.classback), // Replace with your image resource
                contentDescription = "Background",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop ,
                alpha = 0.7f
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    School, // Replace with your icon
                    contentDescription = "Class Icon",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 16.dp),
                    tint = Color(0xFF3E4A61)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = cLass.className,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Teacher: $teacherName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(
                            text = "Students: ${cLass.students.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            People,
                            contentDescription = null,
                            tint = Color(0xFF3E4A61),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                }

                // Right side: IconButton for details
                IconButton(
                    onClick = onDetailClick,
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_forward_ios_24), // Replace with your icon
                        contentDescription = "Class Details",
                        tint = Color(0xFF3E4A61)
                    )
                }
            }
        }

    }
}



@Preview(showBackground = true)
@Composable
fun ClassInfoCardPreview(){
    ClassInfoCard(teacherName = "John", cLass = Class(className = "Math", teacherId = "ad", students = mutableListOf()),
        onDetailClick = {})
}



