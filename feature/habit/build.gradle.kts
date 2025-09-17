plugins {
    id("habitflow.android.feature")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
}

android { namespace = "com.habitflow.feature.habit" }

dependencies {
    implementation(project(":core:core-designsystem"))
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-ui"))
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.compose.material.icons)
    debugImplementation(libs.compose.ui.tooling)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
}

kapt { correctErrorTypes = true }
