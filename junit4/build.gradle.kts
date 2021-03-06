import fr.xgouchet.buildsrc.Dependencies
import fr.xgouchet.buildsrc.settings.commonConfig
import fr.xgouchet.buildsrc.testCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.github.ben-manes.versions")
    id("io.gitlab.arturbosch.detekt")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jetbrains.dokka")
    id("githubWiki")
    jacoco
    maven
}

dependencies {

    compile(project(":core"))
    compile(project(":inject"))

    compile(Dependencies.Libraries.Kotlin)

    compileOnly(Dependencies.Libraries.JUnit4)

    testCompile(Dependencies.Libraries.JUnit5)
    testCompile(Dependencies.Libraries.Spek)
    testCompile(Dependencies.Libraries.TestTools)
    testCompile(Dependencies.Libraries.JUnit4Rules)
}

commonConfig()

githubWiki {
    types = listOf(
            "fr.xgouchet.elmyr.junit4.ForgeRule"
    )
}
