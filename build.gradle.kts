plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.fabric.loom)
}

base {
    val archivesName: String by project
    val modVersion: String by project

    this@base.archivesName = archivesName
    version = modVersion
}

repositories {
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)
    implementation(libs.fabric.kotlin)
}

tasks.processResources {
    val modVersion: String by project

    inputs.property("version", modVersion)
    filesMatching("fabric.mod.json") {
        expand("version" to modVersion)
    }
}

java {
    withSourcesJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}