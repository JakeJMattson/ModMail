package me.aberrantfox.warmbot

import me.aberrantfox.kjdautils.api.startBot
import me.aberrantfox.warmbot.messages.Locale
import net.dv8tion.jda.core.entities.Game
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val token = args.firstOrNull()

    if(token == null || token == "UNSET") {
        println("You must specify the token with the -e flag when running via docker, or as the first command line param.")
        exitProcess(-1)
    }

    startBot(token) {
        configure {
            globalPath = "me.aberrantfox.warmbot"

            registerInjectionObject(this)
            registerInjectionObject(this@startBot.container)
        }

        jda.presence.game = Game.playing(Locale.messages.DEFAULT_DISCORD_PRESENCE)
    }
}