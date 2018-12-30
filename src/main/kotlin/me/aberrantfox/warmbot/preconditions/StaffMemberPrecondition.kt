package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.kjdautils.api.dsl.Precondition
import me.aberrantfox.kjdautils.internal.command.Fail
import me.aberrantfox.kjdautils.internal.command.Pass
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.entities.TextChannel

@Precondition
fun produceIsStaffMemberPrecondition(configuration: Configuration) = exit@{ event: CommandEvent ->
    if (event.channel !is TextChannel) return@exit Fail(Locale.messages.NO_PERMISSIONS)

    val textChannel = event.channel as TextChannel

    if (!configuration.hasGuildConfig(textChannel.guild.id)) return@exit Pass

    val relevantGuildConfiguration = configuration.getGuildConfig(textChannel.guild.id)!!
    val relevantGuild = event.jda.getGuildById(textChannel.guild.id)
    val staffRole = relevantGuild.getRolesByName(relevantGuildConfiguration.staffRoleName, true).first()
    val memberAuthor = relevantGuild.getMember(event.author)

    return@exit if (memberAuthor.roles.contains(staffRole)) Pass else Fail(Locale.messages.MISSING_STAFF_ROLE)
}