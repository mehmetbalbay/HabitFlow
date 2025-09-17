package plugins

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("habitflow.android.library")
        extensions.configure<LibraryExtension> {
            buildFeatures.compose = true
            composeOptions.kotlinCompilerExtensionVersion = "1.5.10"
        }
    }
}
