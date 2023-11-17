plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("stringfog")
    id("android-junk-code")
}

stringfog {
    // 开关
    enable = true
    // 加解密库的实现类路径，需和上面配置的加解密算法库一致。
    implementation = "com.github.megatronking.stringfog.xor.StringFogImpl"
    // 可选：指定需加密的代码包路径，可配置多个，未指定将默认全部加密。
    //fogPackages = arrayOf("uaic.mega.zoom")
    kg = com.github.megatronking.stringfog.plugin.kg.HardCodeKeyGenerator("megazoom")
}

androidJunkCode {
    variantConfig {
        create("release") {
            //注意：这里的release是变体名称，如果没有设置productFlavors就是buildType名称，如果有设置productFlavors就是flavor+buildType，例如（freeRelease、proRelease）
            packageBase = "uaic.mega.zoom"  //生成java类根包名
            packageCount = 32 //生成包数量
            activityCountPerPackage = 3 //每个包下生成Activity类数量
            excludeActivityJavaFile = false
            //是否排除生成Activity的Java文件,默认false(layout和写入AndroidManifest.xml还会执行)，主要用于处理类似神策全埋点编译过慢问题
            otherCountPerPackage = 51  //每个包下生成其它类的数量
            methodCountPerClass = 26  //每个类下生成方法数量
            resPrefix = "uaic_"  //生成的layout、drawable、string等资源名前缀
            drawableCount = 221  //生成drawable资源数量
            stringCount = 282  //生成string数量
        }
    }
}

android {
    namespace = "com.zcba.megazoom"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.zcba.megazoom"
        minSdk = 24
        targetSdk = 33
        versionCode = 3
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        //重命名打包文件，对apk和aab都生效
        setProperty("archivesBaseName", "${applicationId}-${versionName}-${versionCode}")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-process:2.6.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.preference:preference:1.1.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    implementation("com.github.megatronking.stringfog:xor:5.0.0")

    implementation("com.otaliastudios:cameraview:2.7.2")
    implementation("com.github.chrisbanes:PhotoView:latest.release")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // PictureSelector basic (Necessary)
    implementation("io.github.lucksiege:pictureselector:v3.11.1")
    implementation("com.blankj:utilcodex:1.31.1")

    // Navigation library
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    // EXIF Interface
    implementation("androidx.exifinterface:exifinterface:1.3.6")

    // CameraX core library using the camera2 implementation
    val camerax_version = "1.4.0-alpha02"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    // If you want to additionally use the CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    // If you want to additionally use the CameraX VideoCapture library
    implementation("androidx.camera:camera-video:${camerax_version}")
    // If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:${camerax_version}")
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
    // If you want to additionally use the CameraX Extensions library
    implementation("androidx.camera:camera-extensions:${camerax_version}")


    implementation("com.h6ah4i.android.widget.verticalseekbar:verticalseekbar:1.0.0")
    //GT基础功能(必要的)
    implementation("com.github.1079374315:GSLS_Tool:v1.4.5.1")

    //okhttp
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
}