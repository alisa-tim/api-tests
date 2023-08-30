import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    id("io.qameta.allure") version "2.10.0"
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
    testImplementation("io.github.openfeign:feign-core:12.4")
    testImplementation("io.github.openfeign:feign-okhttp:12.4")
    testImplementation("io.github.openfeign:feign-jackson:12.4")
    testImplementation("io.github.openfeign:feign-gson:12.4")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
}

tasks.test {
    useJUnitPlatform()
    systemProperties(
        mapOf(
            "junit.jupiter.execution.parallel.enabled" to true,
            "junit.jupiter.execution.parallel.mode.default" to "CONCURRENT"
        )
    )
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}