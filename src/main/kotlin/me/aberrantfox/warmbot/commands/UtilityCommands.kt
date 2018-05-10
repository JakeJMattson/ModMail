package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands

@CommandSet
fun utilityCommands() = commands {
    command("ping") {
        execute {
            it.respond("pong! (${it.jda.ping}ms)")
        }
    }

    command("source") {
        execute {
            it.respond("http://github.com/AberrantFox/WarmBot")
        }
    }

    command("Author") {
        execute {
            it.respond("AberrantFox")
        }
    }
}