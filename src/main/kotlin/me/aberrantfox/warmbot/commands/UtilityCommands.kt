package me.aberrantfox.warmbot.commands

import com.google.gson.Gson
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.jda.fullName
import java.awt.Color
import java.util.*

object Project {
    data class Properties(val version: String, val author: String, val repository: String)
    val properties: Properties

    init {
        val propertiesClass = Properties::class.java
        val propFile = propertiesClass.getResource("/properties.json").readText()
        properties = Gson().fromJson(propFile, propertiesClass)
    }
}

val startTime = Date()

@CommandSet
fun utilityCommands() = commands {
    command("ping") {
        description = "Check the status of the bot."
        execute {
            it.respond("pong! (${it.jda.ping}ms)")
        }
    }

    command("version") {
        description = "Display the bot version -- this is updated via maven filtering."
        execute {
            it.respond("**Running version**: ${Project.properties.version}")
        }
    }

    command("author") {
        description = "Display project author -- this is updated via maven filtering."
        execute {
            it.respond("**Project author**: ${Project.properties.author}")
        }
    }

    command("source") {
        description = "Display the source code via a GitLab link."
        execute {
            it.respond(Project.properties.repository)
        }
    }

    command("botinfo") {
        description = "Display the bot information."
        execute {
            it.respond(embed {
                title(it.jda.selfUser.fullName())
                description("A Discord report management bot.")
                setColor(Color.red)
                setThumbnail(it.jda.selfUser.effectiveAvatarUrl)

                field {
                    name = "Creator"
                    value = Project.properties.author
                    inline = false
                }
                field {
                    name = "Contributors"
                    value = "Elliott#0001, JakeyWakey#1569"
                    inline = false
                }
                field {
                    name = "Repository link"
                    value = Project.properties.repository
                    inline = false
                }
            })
        }
    }

    command("uptime") {
        description = "Displays how long the bot has been running."
        execute {
            val milliseconds = Date().time - startTime.time
            val seconds = (milliseconds / 1000) % 60
            val minutes = (milliseconds / (1000 * 60)) % 60
            val hours = (milliseconds / (1000 * 60 * 60)) % 24
            val days = (milliseconds / (1000 * 60 * 60 * 24))

            it.respond(embed {
                setColor(Color.WHITE)
                setTitle("I have been running since")
                setDescription(startTime.toString())

                field {
                    name = "That's been"
                    value = "$days day(s), $hours hour(s), $minutes minute(s) and $seconds second(s)"
                }
            })
        }
    }
}