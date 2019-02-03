package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.entities.TextChannel

@Precondition
fun produceIsStaffMemberPrecondition(configuration: Configuration) = exit@{ event: CommandEvent ->
    if (event.channel !is TextChannel) return@exit Fail(Locale.messages.FAIL_TEXT_CHANNEL_ONLY)

    val guildId = (event.channel as TextChannel).guild.id

    if (!configuration.hasGuildConfig(guildId)) return@exit Pass

    val relevantGuildConfiguration = configuration.getGuildConfig(guildId)!!
    val relevantGuild = event.jda.getGuildById(guildId)
    val staffRole = relevantGuild.getRolesByName(relevantGuildConfiguration.staffRoleName, true).first()
    val memberAuthor = relevantGuild.getMember(event.author)

    return@exit if (memberAuthor.roles.contains(staffRole)) Pass else Fail(Locale.messages.FAIL_MISSING_STAFF_ROLE)
}