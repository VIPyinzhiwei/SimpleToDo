pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
        google()
        mavenCentral()
    }
}

rootProject.name = "SimpleToDo"
include(":app")
