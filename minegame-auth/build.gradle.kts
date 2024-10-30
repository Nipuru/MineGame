plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":minegame-common"))

    implementation("net.afyer.afybroker:afybroker-client:2.1")

    // Springboot Web容器
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }

    // web 容器使用 undertow 性能更强
    implementation("org.springframework.boot:spring-boot-starter-undertow")

    // Springboot devtools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Springboot 拦截器
    implementation("org.springframework.boot:spring-boot-starter-aop")

    // Springboot 邮件
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // servlet包
    implementation("javax.servlet:javax.servlet-api")

    // hutool
    implementation("cn.hutool:hutool-all:5.8.25")

    // io常用工具类
    implementation("commons-io:commons-io:2.13.0")

    // 常用工具类
    implementation("org.apache.commons:commons-lang3:3.12.0")

    // Mybatis Plus
    implementation("com.baomidou:mybatis-plus-boot-starter:3.5.3.2")

    // transmittable-thread-local
    implementation("com.alibaba:transmittable-thread-local:2.14.2")

    annotationProcessor ("org.springframework.boot:spring-boot-configuration-processor")
    testAnnotationProcessor ("org.springframework.boot:spring-boot-configuration-processor")

    // pgsql驱动
    runtimeOnly("org.postgresql:postgresql:42.7.4")
}

tasks.build {
    dependsOn(tasks.bootJar)
}