
plugins {
    alias(libs.plugins.android.application)
    // 在 apply plugin: 'com.android.application' 下添加
//    id("com.aliyun.ams.emas-services") // 添加插件
    alias(libs.plugins.jetbrains.kotlin.android)



    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}



android {
    namespace = "com.example.classtrack"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.classtrack"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            // 选择要添加的 .so 库的 CPU 类型
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Bmob
    implementation (libs.android.sdk)
    implementation (libs.rxjava)
    implementation (libs.rxandroid)
    implementation (libs.okhttp)
    implementation (libs.okio)
    implementation (libs.gson)


    //glide image
    implementation (libs.compose)

    //hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation (libs.androidx.hilt.navigation.compose)


    //lottie
    implementation (libs.lottie.compose)

    //zxing QRcode
    implementation (libs.core)
    implementation (libs.zxing.android.embedded)
    implementation (libs.androidx.appcompat)


    implementation ("com.aliyun.ams:alicloud-android-push:3.9.0")

    implementation ("com.aliyun:alibabacloud-push20160801:1.0.8")
}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}