plugins {
    java
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()

}

dependencies {
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")

    // Negociacion de contenido
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")

    // Jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // PostgreSQL
    implementation ("org.postgresql:postgresql")

    // MongoDB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    // Rest
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    // Caffeine (Para poner un ttl y un limite a la cache)
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    // Validación
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // websocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    // Lombock
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Jackson
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.1")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation ("org.hibernate:hibernate-core:6.2.8.Final")

    // Redis Cache con Redisson
    implementation ("org.redisson:redisson-hibernate-53:3.20.1")
    implementation ("org.springframework.boot:spring-boot-starter-data-redis")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // testear MongoDB
    implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:4.18.0")

    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:4.18.0")

}

tasks.withType<Test> {
    useJUnitPlatform()
}
