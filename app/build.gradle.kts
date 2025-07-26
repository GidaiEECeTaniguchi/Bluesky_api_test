plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
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
    implementation(libs.security.crypto)
    implementation(libs.lifecycle.viewmodel.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("org.mockito:mockito-core:5.18.0")
    androidTestImplementation("org.mockito:mockito-android:5.18.0")
    // kbskyの追加
    implementation (libs.kotlin.stdlib.jdk8)
    implementation(libs.core.jvm)
    implementation(libs.auth.jvm)
    implementation("work.socialhub:khttpclient:0.0.5")
    // ==== Room の追加 ====


    implementation(libs.room.runtime)
    //annotationProcessor(libs.room.compiler)
    kapt(libs.room.compiler)
    // ======================

    // Navigation Component
    val lifecycle_version = "2.6.2" // 最新バージョンは適宜確認してください
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    // Navigation Component (今回のHome画面で必要)
    val nav_version = "2.7.7" // 最新バージョンは適宜確認してください
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    // WorkManager
    val work_version = "2.9.0" // 最新バージョンは適宜確認してください
    implementation("androidx.work:work-runtime-ktx:$work_version")

}
