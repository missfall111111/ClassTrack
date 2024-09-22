package com.example.classtrack.ui.component


import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.material3.TabPosition
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.classtrack.R
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cn.bmob.v3.BmobUser
import com.example.classtrack.data.User
import com.example.classtrack.data.UserType
import com.example.classtrack.ui.navigation.AuthRoute
import com.example.classtrack.ui.navigation.Route
import com.example.classtrack.ui.navigation.StuRoute
import com.example.classtrack.ui.navigation.TeaRoute
import com.example.classtrack.ui.util.Sp
import com.example.classtrack.ui.util.getSharedPreference
import com.example.classtrack.ui.util.setSharedPreference

@Composable
fun LoadingAnimation(speed: Float) {

    val compositionLoading by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.anim_loading))
    val progressLoading by animateLottieCompositionAsState(
        composition = compositionLoading,
        isPlaying = true,
        speed = speed,
        restartOnPlay = true,
        iterations = LottieConstants.IterateForever
    )

    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(top = 1.dp)
    ) {

        LottieAnimation(
            composition = compositionLoading,
            progress = {progressLoading},
            modifier = Modifier.size(45.dp)
        )

    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabIndicatorScope.FancyAnimatedIndicatorWithModifier(index: Int) {
    val colors =
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
        )
    var startAnimatable by remember { mutableStateOf<Animatable<Dp, AnimationVector1D>?>(null) }
    var endAnimatable by remember { mutableStateOf<Animatable<Dp, AnimationVector1D>?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val indicatorColor: Color by animateColorAsState(colors[index % colors.size], label = "")

    Box(
        Modifier
            .tabIndicatorLayout { measurable: Measurable,
                                  constraints: Constraints,
                                  tabPositions: List<TabPosition> ->
                val newStart = tabPositions[index].left
                val newEnd = tabPositions[index].right
                val startAnim =
                    startAnimatable
                        ?: Animatable(newStart, Dp.VectorConverter).also { startAnimatable = it }

                val endAnim =
                    endAnimatable
                        ?: Animatable(newEnd, Dp.VectorConverter).also { endAnimatable = it }

                if (endAnim.targetValue != newEnd) {
                    coroutineScope.launch {
                        endAnim.animateTo(
                            newEnd,
                            animationSpec =
                            if (endAnim.targetValue < newEnd) {
                                spring(dampingRatio = 1f, stiffness = 1000f)
                            } else {
                                spring(dampingRatio = 1f, stiffness = 50f)
                            }
                        )
                    }
                }

                if (startAnim.targetValue != newStart) {
                    coroutineScope.launch {
                        startAnim.animateTo(
                            newStart,
                            animationSpec =
                            // Handle directionality here, if we are moving to the right, we
                            // want the right side of the indicator to move faster, if we are
                            // moving to the left, we want the left side to move faster.
                            if (startAnim.targetValue < newStart) {
                                spring(dampingRatio = 1f, stiffness = 50f)
                            } else {
                                spring(dampingRatio = 1f, stiffness = 1000f)
                            }
                        )
                    }
                }

                val indicatorEnd = endAnim.value.roundToPx()
                val indicatorStart = startAnim.value.roundToPx()

                // Apply an offset from the start to correctly position the indicator around the tab
                val placeable =
                    measurable.measure(
                        constraints.copy(
                            maxWidth = indicatorEnd - indicatorStart,
                            minWidth = indicatorEnd - indicatorStart,
                        )
                    )
                layout(constraints.maxWidth, constraints.maxHeight) {
                    placeable.place(indicatorStart, 0)
                }
            }
            .padding(5.dp)
            .fillMaxSize()
            .drawWithContent {
                drawRoundRect(
                    color = indicatorColor,
                    cornerRadius = CornerRadius(5.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
    )
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FancyIndicatorContainerTabs(Titles:List<String>,
                                modifier: Modifier=Modifier,
                                selectedIndex: Int,
                                onTabSelected: (Int) -> Unit,) {
    val titles = Titles

    Column (modifier=modifier){
        SecondaryTabRow(
            selectedTabIndex = selectedIndex,
            indicator = { FancyAnimatedIndicatorWithModifier(selectedIndex) }
        ) {
            titles.forEachIndexed { index, title ->
                Tab(selected = selectedIndex == index, onClick = { onTabSelected(index)}, text = { Text(title) })
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WelcomeLogo() {
    AnimatedVisibility(
        visible = true, // You can control the visibility state
        enter = scaleIn() + fadeIn(),
    ) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = R.drawable.greet),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }
}


@Composable
fun HaznedarText(modifier: Modifier=Modifier) {

    val customFont = Font(R.font.phantasm)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = modifier.padding(bottom = 5.dp)
    ) {

        Text(
            text = stringResource(R.string.app_name),
            color = Color.White,
            fontFamily = FontFamily(customFont),
            fontSize = 28.sp
        )
    }
}

public val People: ImageVector
    get() {
        if (_People != null) {
            return _People!!
        }
        _People = ImageVector.Builder(
            name = "People",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(15f, 14f)
                reflectiveCurveToRelative(1f, 0f, 1f, -1f)
                reflectiveCurveToRelative(-1f, -4f, -5f, -4f)
                reflectiveCurveToRelative(-5f, 3f, -5f, 4f)
                reflectiveCurveToRelative(1f, 1f, 1f, 1f)
                close()
                moveToRelative(-7.978f, -1f)
                lineTo(7f, 12.996f)
                curveToRelative(0.0010f, -0.2640f, 0.1670f, -1.030f, 0.760f, -1.720f)
                curveTo(8.3120f, 10.6290f, 9.2820f, 100f, 110f, 100f)
                curveToRelative(1.7170f, 00f, 2.6870f, 0.630f, 3.240f, 1.2760f)
                curveToRelative(0.5930f, 0.690f, 0.7580f, 1.4570f, 0.760f, 1.720f)
                lineToRelative(-0.008f, 0.002f)
                lineToRelative(-0.014f, 0.002f)
                close()
                moveTo(11f, 7f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = false, 0f, -4f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 4f)
                moveToRelative(3f, -2f)
                arcToRelative(3f, 3f, 0f, isMoreThanHalf = true, isPositiveArc = true, -6f, 0f)
                arcToRelative(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 6f, 0f)
                moveTo(6.936f, 9.28f)
                arcToRelative(6f, 6f, 0f, isMoreThanHalf = false, isPositiveArc = false, -1.23f, -0.247f)
                arcTo(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = false, 5f, 9f)
                curveToRelative(-40f, 00f, -50f, 30f, -50f, 40f)
                quadToRelative(0f, 1f, 1f, 1f)
                horizontalLineToRelative(4.216f)
                arcTo(2.24f, 2.24f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5f, 13f)
                curveToRelative(00f, -1.010f, 0.3770f, -2.0420f, 1.090f, -2.9040f)
                curveToRelative(0.2430f, -0.2940f, 0.5260f, -0.5690f, 0.8460f, -0.8160f)
                moveTo(4.92f, 10f)
                arcTo(5.5f, 5.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 4f, 13f)
                horizontalLineTo(1f)
                curveToRelative(00f, -0.260f, 0.1640f, -1.030f, 0.760f, -1.7240f)
                curveToRelative(0.5450f, -0.6360f, 1.4920f, -1.2560f, 3.160f, -1.2750f)
                close()
                moveTo(1.5f, 5.5f)
                arcToRelative(3f, 3f, 0f, isMoreThanHalf = true, isPositiveArc = true, 6f, 0f)
                arcToRelative(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, -6f, 0f)
                moveToRelative(3f, -2f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = false, 0f, 4f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, -4f)
            }
        }.build()
        return _People!!
    }

private var _People: ImageVector? = null




public val School: ImageVector
    get() {
        if (_School != null) {
            return _School!!
        }
        _School = ImageVector.Builder(
            name = "School",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(480f, 840f)
                lineTo(200f, 688f)
                verticalLineToRelative(-240f)
                lineTo(40f, 360f)
                lineToRelative(440f, -240f)
                lineToRelative(440f, 240f)
                verticalLineToRelative(320f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(-276f)
                lineToRelative(-80f, 44f)
                verticalLineToRelative(240f)
                close()
                moveToRelative(0f, -332f)
                lineToRelative(274f, -148f)
                lineToRelative(-274f, -148f)
                lineToRelative(-274f, 148f)
                close()
                moveToRelative(0f, 241f)
                lineToRelative(200f, -108f)
                verticalLineToRelative(-151f)
                lineTo(480f, 600f)
                lineTo(280f, 490f)
                verticalLineToRelative(151f)
                close()
                moveToRelative(0f, -151f)
            }
        }.build()
        return _School!!
    }

private var _School: ImageVector? = null



public val Qr_code_scanner: ImageVector
    get() {
        if (_Qr_code_scanner != null) {
            return _Qr_code_scanner!!
        }
        _Qr_code_scanner = ImageVector.Builder(
            name = "Qr_code_scanner",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(80f, 280f)
                verticalLineToRelative(-200f)
                horizontalLineToRelative(200f)
                verticalLineToRelative(80f)
                horizontalLineTo(160f)
                verticalLineToRelative(120f)
                close()
                moveToRelative(0f, 600f)
                verticalLineToRelative(-200f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(120f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(80f)
                close()
                moveToRelative(600f, 0f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(-120f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(200f)
                close()
                moveToRelative(120f, -600f)
                verticalLineToRelative(-120f)
                horizontalLineTo(680f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(200f)
                verticalLineToRelative(200f)
                close()
                moveTo(700f, 700f)
                horizontalLineToRelative(60f)
                verticalLineToRelative(60f)
                horizontalLineToRelative(-60f)
                close()
                moveToRelative(0f, -120f)
                horizontalLineToRelative(60f)
                verticalLineToRelative(60f)
                horizontalLineToRelative(-60f)
                close()
                moveToRelative(-60f, 60f)
                horizontalLineToRelative(60f)
                verticalLineToRelative(60f)
                horizontalLineToRelative(-60f)
                close()
                moveToRelative(-60f, 60f)
                horizontalLineToRelative(60f)
                verticalLineToRelative(60f)
                horizontalLineToRelative(-60f)
                close()
                moveToRelative(-60f, -60f)
                horizontalLineToRelative(60f)
                verticalLineToRelative(60f)
                horizontalLineToRelative(-60f)
                close()
                moveToRelative(120f, -120f)
                horizontalLineToRelative(60f)
                verticalLineToRelative(60f)
                horizontalLineToRelative(-60f)
                close()
                moveToRelative(-60f, 60f)
                horizontalLineToRelative(60f)
                verticalLineToRelative(60f)
                horizontalLineToRelative(-60f)
                close()
                moveToRelative(-60f, -60f)
                horizontalLineToRelative(60f)
                verticalLineToRelative(60f)
                horizontalLineToRelative(-60f)
                close()
                moveToRelative(240f, -320f)
                verticalLineToRelative(240f)
                horizontalLineTo(520f)
                verticalLineToRelative(-240f)
                close()
                moveTo(440f, 520f)
                verticalLineToRelative(240f)
                horizontalLineTo(200f)
                verticalLineToRelative(-240f)
                close()
                moveToRelative(0f, -320f)
                verticalLineToRelative(240f)
                horizontalLineTo(200f)
                verticalLineToRelative(-240f)
                close()
                moveToRelative(-60f, 500f)
                verticalLineToRelative(-120f)
                horizontalLineTo(260f)
                verticalLineToRelative(120f)
                close()
                moveToRelative(0f, -320f)
                verticalLineToRelative(-120f)
                horizontalLineTo(260f)
                verticalLineToRelative(120f)
                close()
                moveToRelative(320f, 0f)
                verticalLineToRelative(-120f)
                horizontalLineTo(580f)
                verticalLineToRelative(120f)
                close()
            }
        }.build()
        return _Qr_code_scanner!!
    }

private var _Qr_code_scanner: ImageVector? = null


@Composable
fun DrawerContent(
//    onLogoutClick: () -> Unit,
//    onClassesClick: () -> Unit,
//    onInfoClick: () -> Unit,
//    onChangePasswordClick: () -> Unit
    navController: NavController
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.inversePrimary
    ) {
        val currentUser: BmobUser? = BmobUser.getCurrentUser(BmobUser::class.java)

        val context= LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // 顶部用户问候信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person, // 用户图标
                    contentDescription = "Profile Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Hello,",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (currentUser != null) {
                        Text(
                            text = currentUser.username,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // 菜单项：我的班级
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary)
            DrawerMenuItem(
                School, // 班级图标
                label = "My Classes",
                onClick = { onClassesClick(navController=navController) }
            )

            // 菜单项：个人信息
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary)
            DrawerMenuItem(
                icon = Icons.Default.Info, // 个人信息图标
                label = "My Info",
                onClick ={ oonInfoClick(navController=navController) }
            )



            // 底部登出按钮
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary)
            DrawerMenuItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp, // 登出图标
                label = "Log Out",
                onClick = { onLogoutClick(navController=navController,context=context) },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

fun onLogoutClick(navController: NavController,context: Context) {

    val currentUser = BmobUser.getCurrentUser(User::class.java)
    Log.d("LogOut",currentUser.userType)
    // 登出当前用户
    BmobUser.logOut()


    setSharedPreference(context=context, key = Sp.PASSWORD, value = "NULL")
    val username = getSharedPreference(context, Sp.USERNAME, "NULL").toString()

    // 跳转到登录页面
    navController.navigate(AuthRoute.LOGIN.name+"/$username") {
        // 清除返回栈，防止用户按返回键回到登出前的页面
        popUpTo(if (currentUser.userType==UserType.STUDENT.name) StuRoute.CLASSES.name
        else TeaRoute.CLASS.name){
            inclusive=true
        }
    }
}

//    onClassesClick: () -> Unit,
fun onClassesClick(navController: NavController) {
    val currentUser = BmobUser.getCurrentUser(User::class.java)

    navController.navigate(if (currentUser.userType==UserType.STUDENT.name) StuRoute.CLASSES.name
    else TeaRoute.CLASS.name)
}

//onInfoClick: () -> Unit,
fun oonInfoClick(navController: NavController) {
    navController.navigate(AuthRoute.INFO.name)
}


@Composable
fun BottomBar(content:String,modifier: Modifier=Modifier){
    BottomAppBar(modifier=modifier,
        containerColor = MaterialTheme.colorScheme.inversePrimary) {
        Text(text = content, textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.displaySmall)
    }
}





