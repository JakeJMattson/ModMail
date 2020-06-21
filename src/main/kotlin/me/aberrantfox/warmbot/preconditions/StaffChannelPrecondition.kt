package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.warmbot.services.Configuration
import me.jakejmattson.kutils.api.annotations.Precondition
import me.jakejmattson.kutils.api.dsl.preconditions.*
import net.dv8tion.jda.api.entities.TextChannel

@Precondition
fun produceIsStaffChannelPrecondition(configuration: Configuration) = precondition {
    val textChannel = it.channel as? TextChannel ?: return@precondition Fail()
    val guildConfig = configuration.getGuildConfig(textChannel.guild.id) ?: return@precondition Pass
    val isWhitelisted = textChannel.id in guildConfig.staffChannels
    val isReportCategory = textChannel.parent?.id == guildConfig.reportCategory

    if (!isWhitelisted && !isReportCategory) return@precondition Fail()

    return@precondition Pass
}