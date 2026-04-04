import java.util.*;

plugins {
    id("java")
    id("war")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.liquibase.gradle") version "2.2.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

val springVersion: String by project
val springDataVersion: String by project
val jakartaVersion: String by project
//val hibernateVersion: String by project
val postgresVersion: String by project
//val freemarkerVersion: String by project
//val hikariVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("jakarta.servlet:jakarta.servlet-api:$jakartaVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("org.springframework.security:spring-security-taglibs")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    implementation("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("org.postgresql:postgresql:$postgresVersion")
    liquibaseRuntime("info.picocli:picocli:4.6.1")
}

tasks.test {
    useJUnitPlatform()
}

val properties = run {
    val props = Properties()
    val propsFile = project.file("src/main/resources/db/liquibase.properties")
    if (!propsFile.exists()) {
        throw GradleException("liquibase.properties not found in project root")
    }
    propsFile.inputStream().use { props.load(it) }
    props.entries.associate { it.key.toString() to it.value.toString() }
}

liquibase {
    activities {
        create("main") {
            arguments = mapOf(
                "changelogFile" to properties["changeLogFile"],
                "url" to properties["url"],
                "username" to properties["username"],
                "password" to properties["password"],
                "driver" to properties["driver"]
            )
        }
    }
}