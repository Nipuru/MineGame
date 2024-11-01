plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
    id("org.springframework.boot") version "2.7.16" apply false
    id("io.spring.dependency-management") version "1.1.3" apply false
    id("io.papermc.paperweight.userdev") version "1.7.2" apply false
}

subprojects {

    apply(plugin = "java")
    apply(plugin = "maven-publish")

    group = "top.nipuru.minegame"
    version = "0.1"

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.tabooproject.org/repository/releases/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        testImplementation ("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:5.8.2")

        compileOnly ("org.projectlombok:lombok:1.18.24")
        annotationProcessor ("org.projectlombok:lombok:1.18.24")

        testCompileOnly ("org.projectlombok:lombok:1.18.24")
        testAnnotationProcessor ("org.projectlombok:lombok:1.18.24")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

tasks.withType<Jar> {
    enabled = false
}





