plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

}

android {
    namespace = "com.github.da_dogk"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.github.da_dogk"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        viewBinding = true
    }
}

dependencies {
    val retrofit_version = "2.6.4"
    // Retrofit 라이브러리
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    // Gson 변환기 라이브러리
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")
    // Scalars 변환기 라이브러리
    //implementation ("com.squareup.retrofit2:converter-scalars:$retrofit_version")

    //네비게이션 바
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")

    //okhttp 라이브러리 추가
    val okhttp_version = "4.9.1"
    implementation("com.squareup.okhttp3:okhttp:4.9.1")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}