plugins {
    id("habitflow.android.library")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
}

android { namespace = "com.habitflow.core.network" }

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
}

kapt { correctErrorTypes = true }
