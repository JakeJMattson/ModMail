package me.aberrantfox.warmbot.extensions

import me.aberrantfox.kjdautils.extensions.jda.fullName
import net.dv8tion.jda.core.entities.MessageChannel

fun MessageChannel.archiveString() = iterableHistory.reversed()
                .filter { !it.contentRaw.toLowerCase().contains("!archive")  }
                .joinToString("\n") { "${it.author.fullName()}: ${it.fullContent()}" }
