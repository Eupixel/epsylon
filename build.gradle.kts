// build.gradle.kts
plugins {
    kotlin("jvm") version "2.1.20"
    application
}

group = "dev.aquestry"
version = "1.0"

repositories {
    maven("https://jitpack.io")
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom-snapshots:1_21_5-2398778b46")
    implementation("net.kyori:adventure-text-minimessage:4.20.0")
    implementation("net.lenni0451:MCPing:1.4.4")
    implementation("com.github.docker-java:docker-java:3.3.3")
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
    mainClass.set("dev.aquestry.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}