package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.api.entities.TextChannel

@Precondition
fun produceIsStaffChannelPrecondition(configuration: Configuration) = precondition {
    if (it.channel !is TextChannel) return@precondition Fail()

    val textChannel = it.channel as TextChannel

    val guildConfig = configuration.getGuildConfig(textChannel.guild.id) ?: return@precondition Pass

    val isWhitelisted = textChannel.id in guildConfig.staffChannels
    val isReportCategory = textChannel.parent?.id == guildConfig.reportCategory

    if (!isWhitelisted && !isReportCategory) return@precondition Fail()

    return@precondition Pass
}