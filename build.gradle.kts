import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    id("com.google.devtools.ksp").version("1.6.0-1.0.1")
    application
}

group = "me.ruitiari"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
    implementation("com.sun.xml.bind:jaxb-impl:3.0.2")
    implementation("com.github.ajalt.mordant:mordant:2.0.0-beta4")
    implementation("com.github.ajalt.clikt:clikt:3.3.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp:logging-interceptor:2.7.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.0-native-mt")
    implementation("com.squareup.moshi:moshi:1.13.0")
    implementation ("org.apache.commons:commons-text:1.9")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.13.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}