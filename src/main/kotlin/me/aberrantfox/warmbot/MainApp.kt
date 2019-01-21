package me.aberrantfox.warmbot

import me.aberrantfox.kjdautils.api.startBot
import net.dv8tion.jda.core.entities.Game

fun main(args: Array<String>) {
    val token = args.first()
    start(token)
}

private fun start(token: String) = startBot(token) {
	configure {
		prefix = "!"
        globalPath = "me.aberrantfox.warmbot"
	}

    jda.presence.setPresence(Game.of(Game.GameType.DEFAULT, "DM to contact Staff"), true)
}