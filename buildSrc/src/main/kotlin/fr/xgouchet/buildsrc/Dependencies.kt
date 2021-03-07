package fr.xgouchet.buildsrc

object Dependencies {

    object Versions {
        // Commons
        const val Kotlin = "1.4.10"

        // Tests
        const val JUnit4 = "4.13"
        const val JUnitJupiter = "5.7.0"
        const val JUnitPlatform = "1.7.0"
        const val JUnitVintage = "5.7.0"

        const val Spek2 = "2.0.15"
        const val AssertJ = "0.2.1"
        const val MockitoKotlin = "2.2.0"
        const val Jacoco = "0.8.4"

        // Tools
        const val Detekt = "1.1.1"
        const val KtLint = "9.1.0"
        const val DependencyVersion = "0.27.0"

        const val SystemRules = "1.19.0"
        const val MockitoExt = "3.7.7"

        // Docs
        const val Dokka = "0.10.1"
    }

    object Libraries {

        const val Kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.Kotlin}"
        const val KotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.Kotlin}"

        const val JUnit4 = "junit:junit:${Versions.JUnit4}"

        @JvmField val Spek = arrayOf(
                "org.spekframework.spek2:spek-dsl-jvm:${Versions.Spek2}",
                "org.spekframework.spek2:spek-runner-junit5:${Versions.Spek2}",
                "org.jetbrains.kotlin:kotlin-reflect:${Versions.Kotlin}"
        )

        @JvmField val JUnit5 = arrayOf(
                "org.junit.platform:junit-platform-launcher:${Versions.JUnitPlatform}",
                "org.junit.vintage:junit-vintage-engine:${Versions.JUnitVintage}",
                "org.junit.jupiter:junit-jupiter:${Versions.JUnitJupiter}"
        )

        @JvmField val TestTools = arrayOf(
                "net.wuerl.kotlin:assertj-core-kotlin:${Versions.AssertJ}",
                "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.MockitoKotlin}"
        )

        @JvmField val JUnit4Rules = arrayOf(
                "com.github.stefanbirkner:system-rules:${Versions.SystemRules}"
        )

        @JvmField val JUnit5Extensions = arrayOf(
                "org.mockito:mockito-junit-jupiter:${Versions.MockitoExt}"
        )
    }

    object ClassPaths {
        const val Kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin}"
        const val KtLint = "org.jlleitschuh.gradle:ktlint-gradle:${Versions.KtLint}"
        const val JUnitPlatform = "org.junit.platform:junit-platform-gradle-plugin:${Versions.JUnitPlatform}"
    }

    object Repositories {
        const val Fabric = "https://maven.fabric.io/public"
        const val Jitpack = "https://jitpack.io"
        const val Gradle = "https://plugins.gradle.org/m2/"
        const val Google = "https://maven.google.com"
        const val Dokka = "https://dl.bintray.com/kotlin/dokka"
    }

    object PluginId {
        const val Detetk = "io.gitlab.arturbosch.detekt"
        const val KtLint = "org.jlleitschuh.gradle.ktlint"
        const val DependencyVersion = "com.github.ben-manes.versions"
        const val Kotlin = "org.jetbrains.kotlin.jvm"
        const val Dokka = "org.jetbrains.dokka"
    }

    object PluginNamespaces {
        const val Gradle = "org.gradle"
    }
}
