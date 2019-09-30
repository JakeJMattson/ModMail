package me.aberrantfox.warmbot.commands

import com.google.gson.Gson
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.warmbot.extensions.toMinimalTimeString
import me.aberrantfox.warmbot.messages.Locale
import java.awt.Color
import java.util.Date

private data class Properties(val version: String, val author: String, val repository: String)
private val propFile = Properties::class.java.getResource("/properties.json").readText()
private val Project = Gson().fromJson(propFile, Properties::class.java)
private val startTime = Date()

@CommandSet("Utility")
fun utilityCommands() = commands {
    command("Ping") {
        requiresGuild = true
        description = Locale.messages.PING_DESCRIPTION
        execute {
            it.respond("JDA ping: ${it.discord.jda.gatewayPing}ms\n")
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
                color = Color.green
                thumbnail = it.discord.jda.selfUser.effectiveAvatarUrl

                addField(it.discord.jda.selfUser.fullName(), "A Discord report management bot.", true)
                addField("Version", Project.version, true)
                addField("Contributors", "${Project.author}, Elliott#0001, JakeyWakey#1569", true)
                addField("", "[Source code](${Project.repository})", true)
            })
        }
    }

    command("Uptime") {
        description = "Displays how long the bot has been running."
        execute {
            val seconds = (Date().time - startTime.time) / 1000

            it.respond(embed {
                title = "I have been running since"
                description = startTime.toString()
                color = Color.WHITE

                field {
                    name = "That's been"
                    value = seconds.toMinimalTimeString()
                }
            })
        }
    }
}