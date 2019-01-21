package me.aberrantfox.warmbot.commands

import com.google.gson.Gson
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.jda.fullName
import java.awt.Color
import java.util.Date

private data class Properties(val version: String, val author: String, val repository: String)
private val propFile = Properties::class.java.getResource("/properties.json").readText()
private val Project = Gson().fromJson(propFile, Properties::class.java)
private val startTime = Date()

@CommandSet("utility")
fun utilityCommands() = commands {
    command("ping") {
        requiresGuild = true
        description = "Check the status of the bot."
        execute {
            it.respond("pong! (${it.jda.ping}ms)")
        }
    }

    command("version") {
        requiresGuild = true
        description = "Display the bot version."
        execute {
            it.respond("**Running version**: ${Project.version}")
        }
    }

    command("author") {
        requiresGuild = true
        description = "Display project author."
        execute {
            it.respond("**Project author**: ${Project.author}")
        }
    }

    command("source") {
        requiresGuild = true
        description = "Display the source code via a GitLab link."
        execute {
            it.respond(Project.repository)
        }
    }

    command("botinfo") {
        requiresGuild = true
        description = "Display the bot information."
        execute {
            it.respond(embed {
                title(it.jda.selfUser.fullName())
                description("A Discord report management bot.")
                setColor(Color.green)
                setThumbnail(it.jda.selfUser.effectiveAvatarUrl)

                field {
                    name = "Creator"
                    value = Project.author
                    inline = false
                }
                field {
                    name = "Contributors"
                    value = "Elliott#0001, JakeyWakey#1569"
                    inline = false
                }
                field {
                    name = "Source"
                    value = Project.repository
                    inline = false
                }
                field {
                    name = "Version"
                    value = Project.version
                    inline = false
                }
            })
        }
    }

    command("uptime") {
        requiresGuild = true
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