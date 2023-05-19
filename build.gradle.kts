group = "me.jakejmattson"
version = "4.0.0"
description = "A report management bot"

plugins {
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.serialization") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.github.ben-manes.versions") version "0.46.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("me.jakejmattson:DiscordKt:0.23.4")
}

tasks {
    compileJava {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "17"
        dependsOn("writeProperties")
    }

    register<WriteProperties>("writeProperties") {
        dependsOn(processResources)
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