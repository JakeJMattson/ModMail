package me.aberrantfox.warmbot

import me.aberrantfox.kjdautils.api.startBot
import me.aberrantfox.warmbot.extensions.conversionJDA
import net.dv8tion.jda.core.entities.Game

fun main(args: Array<String>) {
    val token = args.first()

    startBot(token) {
        conversionJDA = jda

        configure {
            prefix = "!"
        }

        jda.presence.setPresence(Game.of(Game.GameType.DEFAULT, "DM to contact Staff"), true)
    }
}