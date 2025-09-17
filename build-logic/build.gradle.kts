plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

gradlePlugin {
    plugins {
        create("androidLibrary") {
            id = "habitflow.android.library"
            implementationClass = "plugins.AndroidLibraryConventionPlugin"
        }
        create("androidFeature") {
            id = "habitflow.android.feature"
            implementationClass = "plugins.AndroidFeatureConventionPlugin"
        }
        create("jvmLibrary") {
            id = "habitflow.jvm.library"
            implementationClass = "plugins.JvmLibraryConventionPlugin"
        }
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:8.2.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
}
