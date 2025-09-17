plugins {
    id("habitflow.android.feature")
}

android { namespace = "com.habitflow.core.designsystem" }

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation("androidx.compose.ui:ui-text-google-fonts")
}
