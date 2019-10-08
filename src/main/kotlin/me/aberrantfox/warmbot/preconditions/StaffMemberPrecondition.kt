package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.jda.toMember
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.api.entities.TextChannel

@Precondition
fun produceIsStaffMemberPrecondition(configuration: Configuration) = precondition {
    if (it.channel !is TextChannel) return@precondition Fail(Locale.messages.FAIL_TEXT_CHANNEL_ONLY)

    val guild = (it.channel as TextChannel).guild

    val guildConfig = configuration.getGuildConfig(guild.id) ?: return@precondition Pass

    val staffRole = guild.getRolesByName(guildConfig.staffRoleName, true).first()

    if (staffRole !in it.author.toMember(guild)!!.roles) return@precondition Fail(Locale.messages.FAIL_MISSING_STAFF_ROLE)

    return@precondition Pass
}