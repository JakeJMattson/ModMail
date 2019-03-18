package me.aberrantfox.warmbot

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.KJDAConfiguration
import me.aberrantfox.kjdautils.api.startBot
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

        jda.presence.setPresence(Game.of(Game.GameType.DEFAULT, "DM to contact Staff"), true)
    }
}

@Service
class prefixLoader(configuration: Configuration) { init { kjdaConfig.prefix = configuration.prefix } }