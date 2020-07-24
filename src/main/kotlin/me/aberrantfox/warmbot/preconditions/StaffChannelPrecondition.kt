package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.warmbot.services.Configuration
import me.jakejmattson.kutils.api.dsl.command.CommandEvent
import me.jakejmattson.kutils.api.dsl.preconditions.*
import net.dv8tion.jda.api.entities.TextChannel

class StaffChannelPrecondition(private val configuration: Configuration) : Precondition() {
    override fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val textChannel = event.channel as? TextChannel ?: return Fail()
        val guildConfig = configuration.getGuildConfig(textChannel.guild.id) ?: return Pass
        val isReportCategory = textChannel.parent?.id == guildConfig.reportCategory

        return if (isReportCategory) Pass else Fail()
    }
}