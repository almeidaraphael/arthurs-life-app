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
    versionCatalogs {
        create("vclibs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "LemonQwest"
include(":app")
