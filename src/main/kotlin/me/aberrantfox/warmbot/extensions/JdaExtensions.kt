package me.aberrantfox.warmbot.extensions

import me.jakejmattson.kutils.api.Discord
import me.jakejmattson.kutils.api.annotations.Service
import net.dv8tion.jda.api.JDA

private lateinit var jda: JDA

@Service
class JdaInitializer(discord: Discord) { init { jda = discord.jda } }

fun String.idToUser() = jda.getUserById(this)
fun String.idToTextChannel() = jda.getTextChannelById(this)
fun String.idToPrivateChannel() = jda.getPrivateChannelById(this)
fun String.idToCategory() = jda.getCategoryById(this)
fun String.idToGuild() = jda.getGuildById(this)

fun String.isValidChannel() = try {
    this.idToTextChannel(); true
} catch (e: Exception) {
    false
}

fun selfUser() = jda.selfUser
fun getPrivateChannels() = jda.privateChannels
fun guilds() = jda.guilds