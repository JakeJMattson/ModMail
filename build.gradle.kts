import org.jetbrains.kotlin.config.KotlinCompilerVersion

group = "me.jakejmattson"
version = "4.0.0"

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
}

repositories {
    mavenLocal()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
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
}