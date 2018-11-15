package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*

@CommandSet
fun utilityCommands() = commands {
    command("ping") {
        description = "Check the status of the bot."
        execute {
            it.respond("pong! (${it.jda.ping}ms)")
        }
    }

    command("source") {
        description = "Display the source code via a GitLab link."
        execute {
            it.respond("https://gitlab.com/AberrantFox/WarmBot")
        }
    }

    command("Author") {
        description = "Display the bot author."
        execute {
            it.respond("AberrantFox")
        }
    }
}