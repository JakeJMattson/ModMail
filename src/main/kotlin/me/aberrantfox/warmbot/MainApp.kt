package me.aberrantfox.warmbot

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.KJDAConfiguration
import me.aberrantfox.kjdautils.api.startBot
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.entities.Game

private lateinit var kjdaConfig: KJDAConfiguration

fun main(args: Array<String>) {
    val token = args.first()

    if(token == "UNSET") {
        println("You must specify the token with the -e flag when running via docker.")
        System.exit(-1)
    }

    startBot(token) {
        configure {
            kjdaConfig = this
            globalPath = "me.aberrantfox.warmbot"
        }

        jda.presence.game = Game.playing(Locale.messages.DEFAULT_DISCORD_PRESENCE)
    }
}

@Service
class prefixLoader(configuration: Configuration) { init { kjdaConfig.prefix = configuration.prefix } }