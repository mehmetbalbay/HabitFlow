plugins {
    id("habitflow.android.library")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
}

android { namespace = "com.habitflow.sync" }

dependencies {
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-database"))
    implementation(project(":core:core-ui"))
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.room:room-runtime:2.6.1")
}

kapt { correctErrorTypes = true }
