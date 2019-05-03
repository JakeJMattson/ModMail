package me.aberrantfox.warmbot

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.KJDAConfiguration
import me.aberrantfox.kjdautils.api.startBot
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.entities.Game

private lateinit var kjdaConfig: KJDAConfiguration

fun main(args: Array<String>) {
    val token = args.firstOrNull()

    if(token == null || token == "UNSET") {
        println("You must specify the token with the -e flag when running via docker, or as the first command line param.")
        System.exit(-1)
        return
    }

    startBot(token) {
        configure {
            kjdaConfig = this
            globalPath = "me.aberrantfox.warmbot"
        }

        container.commands.getValue("help").category = "Utility"
        jda.presence.game = Game.playing(Locale.messages.DEFAULT_DISCORD_PRESENCE)
    }
}

@Service
class prefixLoader(configuration: Configuration) { init { kjdaConfig.prefix = configuration.prefix } }