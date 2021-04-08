import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.31"
    kotlin("plugin.spring") version "1.4.31"

    // JPA
    kotlin("plugin.jpa") version "1.4.31"

    // Liquibase
    id("org.liquibase.gradle") version "2.0.4"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    // Default
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // H2
    runtimeOnly("com.h2database:h2")

    // Liquibase
    implementation("org.liquibase:liquibase-core:4.3.1")

    // Feign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:3.0.2")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation:2.4.4")

    // Mockito
    testImplementation("org.mockito:mockito-core:3.8.0")

    // WireMock
    testImplementation("com.github.tomakehurst:wiremock-standalone:2.27.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
