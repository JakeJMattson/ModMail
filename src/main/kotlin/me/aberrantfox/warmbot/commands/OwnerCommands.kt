package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.warmbot.services.Configuration

@CommandSet("owner")
fun configurationCommands(configuration: Configuration) = commands {
    command("test") {
        requiresGuild = true
        description = "Test the owner precondition."
        execute {
            it.respond("You own this bot.")
        }
    }
}