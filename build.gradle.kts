plugins {
    kotlin("jvm") version "2.1.20"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.eupixel"
version = "1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.eupixel:vivlib:v0.7.7")
    implementation("net.minestom:minestom-snapshots:1_21_5-69b9a5d844")
    implementation("net.kyori:adventure-text-minimessage:4.20.0")
    implementation("com.github.docker-java:docker-java:3.3.0")
    implementation("org.slf4j:slf4j-simple:2.0.6")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("net.eupixel.MainKt")
}

tasks.shadowJar {
    archiveClassifier.set("all")
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}