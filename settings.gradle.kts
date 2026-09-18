pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GHClient"
include(":app")
include(":core:domain")
include(":core:network")
include(":core:database")
include(":core:datastore")
include(":core:ui")
include(":core:util")
include(":feature:auth:api")
include(":feature:auth:impl")
include(":feature:search:api")
include(":feature:search:impl")
include(":feature:repository:api")
include(":feature:repository:impl")
include(":feature:issues:api")
include(":feature:issues:impl")
include(":feature:upload:api")
include(":feature:upload:impl")
include(":feature:favorites:api")
include(":feature:favorites:impl")
include(":feature:profile:api")
include(":feature:profile:impl")
