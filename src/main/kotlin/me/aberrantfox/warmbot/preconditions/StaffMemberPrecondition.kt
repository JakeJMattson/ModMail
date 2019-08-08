package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.jda.toMember
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.api.entities.TextChannel

@Precondition
fun produceIsStaffMemberPrecondition(configuration: Configuration) = exit@{ event: CommandEvent ->
    if (event.channel !is TextChannel) return@exit Fail(Locale.messages.FAIL_TEXT_CHANNEL_ONLY)

    val guild = (event.channel as TextChannel).guild

    val guildConfig = configuration.getGuildConfig(guild.id) ?: return@exit Pass

    val staffRole = guild.getRolesByName(guildConfig.staffRoleName, true).first()

    if (staffRole !in event.author.toMember(guild)!!.roles) return@exit Fail(Locale.messages.FAIL_MISSING_STAFF_ROLE)

    return@exit Pass
}