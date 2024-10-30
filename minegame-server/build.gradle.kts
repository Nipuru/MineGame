plugins {
    `java-library`
    id("com.github.johnrengelman.shadow")
    id("io.papermc.paperweight.userdev")
}

dependencies {
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:20.1.0")
    compileOnly("net.afyer.afybroker:afybroker-client:2.1")
    implementation(project(":minegame-common"))
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version
    )
    inputs.properties(props)
    filesMatching("plugin.yml") {
        expand(props)
    }
}