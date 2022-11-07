group = "me.jakejmattson"
version = "4.0.0"
description = "A report management bot"

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.github.ben-manes.versions") version "0.43.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("me.jakejmattson:DiscordKt:0.23.4")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        dependsOn("writeProperties")
    }

    register<WriteProperties>("writeProperties") {
        property("name", project.name)
        property("description", project.description.toString())
        property("version", version.toString())
        property("url", "https://github.com/JakeJMattson/ModMail")
        setOutputFile("src/main/resources/bot.properties")
    }

    shadowJar {
        archiveFileName.set("ModMail.jar")
        manifest {
            attributes("Main-Class" to "me.jakejmattson.modmail.MainKt")
        }
    }
}