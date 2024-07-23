pluginManagement {
    repositories {
        google()  // Google Maven 저장소 추가
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()  // Google Maven 저장소 추가
        mavenCentral()
    }
}
rootProject.name = "tclick"
include(":app")
