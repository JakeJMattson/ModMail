package me.aberrantfox.warmbot.extensions

import me.aberrantfox.kjdautils.extensions.jda.fullName
import net.dv8tion.jda.core.entities.MessageChannel

fun MessageChannel.archiveString(prefix: String) = iterableHistory.reversed()
        .filter { !it.contentRaw.toLowerCase().matches(Regex("($prefix){1,2}archive")) }
        .joinToString("\n") { "${it.author.fullName()}: ${it.fullContent()}" }
