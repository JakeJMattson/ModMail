package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.Precondition
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.api.entities.TextChannel

@Precondition
fun produceIsStaffChannelPrecondition(configuration: Configuration) = exit@{ event: CommandEvent<*> ->
    if (event.channel !is TextChannel) return@exit Fail()

    val textChannel = event.channel as TextChannel

    val guildConfig = configuration.getGuildConfig(textChannel.guild.id) ?: return@exit Pass

    val isWhitelisted = textChannel.id in guildConfig.staffChannels
    val isReportCategory = textChannel.parent?.id == guildConfig.reportCategory

    if (!isWhitelisted && !isReportCategory) return@exit Fail()

    return@exit Pass
}