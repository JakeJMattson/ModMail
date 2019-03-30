package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.entities.TextChannel

@Precondition
fun produceIsStaffChannelPrecondition(configuration: Configuration) = exit@{ event: CommandEvent ->
    if (event.channel !is TextChannel) return@exit Fail()

    val textChannel = event.channel as TextChannel

    val guildConfig = configuration.getGuildConfig(textChannel.guild.id) ?: return@exit Pass

    if (textChannel.id !in guildConfig.staffChannels) return@exit Fail()

    return@exit Pass
}