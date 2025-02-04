plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.24"
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "com.shinriyo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intell
intellij {
    version.set("2024.1.4")
    type.set("IC") // Target IDE Platform

    plugins.set(
        listOf(
            "org.jetbrains.kotlin",
            "Dart:241.18808", // IntelliJ IDEA 2024.1.4に対応するDartプラグインのバージョン
            "io.flutter:80.0.2"  // IntelliJ IDEA 2024.1.4に対応するFlutterプラグインのバージョン
        )
    )
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("242.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
