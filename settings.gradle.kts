pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "HabitFlow"
include(":app")
include(":core:core-domain")
include(":core:core-database")
include(":core:core-data")
include(":core:core-ui")
include(":core:core-designsystem")
include(":core:core-network")
include(":feature:home")
include(":feature:profile")
include(":feature:auth")
include(":feature:habit")
include(":feature:water")
include(":feature:exercise")
include(":feature:meals")
include(":feature:insights")
include(":feature:settings")
include(":feature:onboarding")
include(":sync")
includeBuild("build-logic")
