plugins {
    id("habitflow.android.feature")
}

android { namespace = "com.habitflow.core.ui" }

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.foundation)
    implementation(libs.material3)
    debugImplementation(libs.compose.ui.tooling)
    implementation(project(":core:core-domain"))
}
