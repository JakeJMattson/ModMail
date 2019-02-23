package me.aberrantfox.warmbot

import me.aberrantfox.kjdautils.api.startBot
import net.dv8tion.jda.core.entities.Game

fun main(args: Array<String>) {
    val token = args.first()

    if(token == "UNSET") {
        println("You must specify the token with the -e flag when running via docker.")
        System.exit(-1)
    }

    startBot(token) {
        configure {
            prefix = "!"
            globalPath = "me.aberrantfox.warmbot"
        }

        jda.presence.setPresence(Game.of(Game.GameType.DEFAULT, "DM to contact Staff"), true)
    }
}