import java.util.*

plugins {
    id("java")
    id("war")
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.liquibase.gradle") version "2.2.0"
    id("jacoco")
}

group = "org.example"
version = "1.0-SNAPSHOT"

val springVersion: String by project
val springDataVersion: String by project
val jakartaVersion: String by project
val postgresVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("jakarta.servlet:jakarta.servlet-api:$jakartaVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")
    //implementation("org.springframework.security:spring-security-taglibs")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-freemarker")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-aop")


    implementation("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("org.postgresql:postgresql:$postgresVersion")
    liquibaseRuntime("info.picocli:picocli:4.6.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

}

val properties = run {
    val props = Properties()
    val propsFile = project.file("src/main/resources/db/liquibase.properties")
    if (!propsFile.exists()) {
        throw GradleException("liquibase.properties not found at src/main/resources/db/liquibase.properties")
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


tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val jacocoExcludes = listOf(
    "**/ru/kpfu/itis/shakirov/dto/**",
    "**/ru/kpfu/itis/shakirov/model/**",
    "**/ru/kpfu/itis/shakirov/config/**",
    "**/ru/kpfu/itis/shakirov/security/**"
)

jacoco {
    toolVersion = "0.8.12"
    reportsDirectory.set(layout.buildDirectory.dir("jacoco"))
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it).matching {
            exclude(jacocoExcludes)
        }
    }))
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = BigDecimal.valueOf(0.1)
            }
        }
    }
    classDirectories.setFrom(files(classDirectories.files.map {
        fileTree(it).matching {
            exclude(jacocoExcludes)
        }
    }))
}