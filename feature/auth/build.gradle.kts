plugins {
    id("habitflow.android.feature")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.kapt")
}

android { namespace = "com.habitflow.feature.auth" }

dependencies {
    implementation(project(":core:core-designsystem"))
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-data"))
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.compose.material.icons)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.coroutines.core)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    // Firebase exceptions referenced in ViewModel
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
}

kapt { correctErrorTypes = true }
