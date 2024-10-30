rootProject.name = "minegame"

include("minegame-common")
include("minegame-database")
include("minegame-auth")
include("minegame-shared")
include("minegame-broker")
include("minegame-server")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

