plugins {
    id("habitflow.android.feature")
}

android { namespace = "com.habitflow.feature.profile" }

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.material3)
    debugImplementation(libs.compose.ui.tooling)
}

