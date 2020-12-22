import org.jetbrains.kotlin.config.KotlinCompilerVersion

group = "me.jakejmattson"
version = "4.0.0"

plugins {
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.21"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("com.github.ben-manes.versions") version "0.36.0"
}

repositories {
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://dl.bintray.com/kordlib/Kord")
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("me.jakejmattson:DiscordKt:0.22.0-SNAPSHOT")
    implementation("org.apache.velocity:velocity:1.7")
}

tasks {
    val resourcePath = "src/main/resources"

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    copy {
        from(file("$resourcePath/properties-template.json"))
        into(file(resourcePath))
        rename { "properties.json" }
        expand(
            "version" to version,
            "kotlin" to KotlinCompilerVersion.VERSION,
            "repository" to "https://github.com/JakeJMattson/ModMail"
        )
    }

    shadowJar {
        archiveFileName.set("ModMail.jar")
        manifest {
            attributes(
                "Main-Class" to "me.jakejmattson.modmail.MainAppKt"
            )
        }
    }
}