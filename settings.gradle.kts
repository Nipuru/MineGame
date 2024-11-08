rootProject.name = "minegame"

sequenceOf(
    "common",
    "database",
    "auth",
    "shared",
    "broker",
    "game"
).forEach {
    include("minegame-$it")
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

