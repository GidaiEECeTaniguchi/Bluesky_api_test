plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android") version "1.9.23"
    id("org.jetbrains.kotlin.kapt") version "1.9.23"
}

android {
    namespace = "com.testapp.bluesky_api_test"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.testapp.bluesky_api_test"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Room: schema export の設定
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        // kbsky が要求する JVM バージョン、またはプロジェクトのJavaバージョンに合わせる
        // Java 8互換でコンパイルされていることが多いので、"1.8" もしくは "11" を試す
        jvmTarget = "11" // 例: Java 11を使用する場合
        // または jvmTarget = "1.8" // 例: Java 8互換が必要な場合
    }

}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // kbskyの追加
    implementation (libs.kotlin.stdlib.jdk8)
    implementation(libs.core.jvm)
    implementation(libs.auth.jvm)
    // ==== Room の追加 ====


    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    // ======================
}
