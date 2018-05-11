package me.aberrantfox.warmbot.extensions

import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.warmbot.ObjectRegister
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.entities.MessageChannel

fun MessageChannel.archiveString() = iterableHistory.reversed()
        .filter {
            val prefix = (ObjectRegister["config"] as Configuration).prefix
            !it.contentRaw.toLowerCase().matches(Regex("($prefix){1,2}archive"))
        }
        .joinToString("\n") { "${it.author.fullName()}: ${it.fullContent()}" }
