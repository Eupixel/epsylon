// build.gradle.kts
plugins {
    kotlin("jvm") version "2.1.20"
    application
}

group = "dev.aquestry"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom-snapshots:1_21_5-2398778b46")
    implementation("net.kyori:adventure-text-minimessage:4.20.0")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.13")
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