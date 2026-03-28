plugins {
    id("java")
    id("war")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
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
}

tasks.test {
    useJUnitPlatform()
}