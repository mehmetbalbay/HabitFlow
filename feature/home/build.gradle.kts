plugins {
    id("habitflow.android.feature")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
}

android { namespace = "com.habitflow.feature.home" }

dependencies {
    implementation(project(":core:core-designsystem"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation(libs.material3)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}

kapt { correctErrorTypes = true }
