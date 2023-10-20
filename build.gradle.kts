import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    id("io.qameta.allure") version "2.11.2"
    id("io.qameta.allure-report") version "2.11.2"
    application
}

allure {
    adapter {
        autoconfigure.set(true)
        aspectjWeaver.set(true)
        version.set("2.23.0")
    }
}

group = "tests"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("com.squareup.okhttp3:okhttp:4.11.0")
    testImplementation("com.squareup.retrofit2:retrofit:2.9.0")
    testImplementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
}

tasks.test {
    useJUnitPlatform {
        System.getProperty("TAG")?.let { includeTags(it) }
    }
    systemProperties(
        mapOf(
            "junit.jupiter.execution.parallel.enabled" to true,
            "junit.jupiter.execution.parallel.mode.default" to "CONCURRENT"
        )
    )
    finalizedBy(tasks.allureReport)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}