package me.aberrantfox.warmbot.extensions

import net.dv8tion.jda.core.JDA

lateinit var conversionJDA: JDA

fun String.idToUser() = conversionJDA.getUserById(this)
fun String.idToTextChannel() = conversionJDA.getTextChannelById(this)
fun String.idToPrivateChannel() = conversionJDA.getPrivateChannelById(this)
fun String.idToCategory() = conversionJDA.getCategoryById(this)
fun String.idToGuild() = conversionJDA.getGuildById(this)

fun selfUser() = conversionJDA.selfUser
fun getPrivateChannels() = conversionJDA.privateChannels