plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.22" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.22" apply false
    id("com.hiya.jacoco-android") version "0.2" apply false
    id("org.sonarqube") version "4.4.1.3373" apply false
}

// SonarQube konfigürasyonu
sonarqube {
    properties {
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.login", "YOUR_SONARQUBE_TOKEN")
        property("sonar.projectKey", "com.habitflow")
        property("sonar.projectName", "HabitFlow")
        property("sonar.projectVersion", "1.0")
        property("sonar.coverage.jacoco.xmlReportPaths", [
            "${rootDir}/feature/onboarding/build/reports/coverage/test/debug/report.xml"
        ])
    }
}
