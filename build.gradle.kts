group = "me.jakejmattson"
version = "3.1.0"

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("me.jakejmattson:DiscordKt:0.21.0-SNAPSHOT")
    implementation("org.apache.velocity:velocity:1.7")
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}