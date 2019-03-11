package me.aberrantfox.warmbot.commands

import com.google.gson.Gson
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.warmbot.messages.Locale
import java.awt.Color
import java.util.Date

private data class Properties(val version: String, val author: String, val repository: String)
private val propFile = Properties::class.java.getResource("/properties.json").readText()
private val Project = Gson().fromJson(propFile, Properties::class.java)
private val startTime = Date()

@CommandSet("utility")
fun utilityCommands() = commands {
    command("Ping") {
        requiresGuild = true
        description = Locale.messages.PING_DESCRIPTION
        execute {
            it.respond("pong! (${it.jda.ping}ms)")
        }
    }

    command("Version") {
        requiresGuild = true
        description = Locale.messages.VERSION_DESCRIPTION
        execute {
            it.respond("**Running version**: ${Project.version}")
        }
    }

    command("Author") {
        requiresGuild = true
        description = Locale.messages.AUTHOR_DESCRIPTION
        execute {
            it.respond("**Project author**: ${Project.author}")
        }
    }

    command("Source") {
        requiresGuild = true
        description = Locale.messages.SOURCE_DESCRIPTION
        execute {
            it.respond(Project.repository)
        }
    }

    command("BotInfo") {
        requiresGuild = true
        description = Locale.messages.BOT_INFO_DESCRIPTION
        execute {
            it.respond(embed {
                title(it.jda.selfUser.fullName())
                description("A Discord report management bot.")
                setColor(Color.green)
                setThumbnail(it.jda.selfUser.effectiveAvatarUrl)
                addField("Creator", Project.author, false)
                addField("Contributors", "Elliott#0001, JakeyWakey#1569", false)
                addField("Source", Project.repository, false)
                addField("Version", Project.version, false)
            })
        }
    }

    command("Uptime") {
        requiresGuild = true
        description = Locale.messages.UPTIME_DESCRIPTION
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