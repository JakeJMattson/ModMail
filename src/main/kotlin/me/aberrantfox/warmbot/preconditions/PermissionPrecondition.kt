package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.warmbot.extensions.requiredPermissionLevel
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import me.jakejmattson.kutils.api.dsl.command.CommandEvent
import me.jakejmattson.kutils.api.dsl.preconditions.*
import me.jakejmattson.kutils.api.extensions.jda.toMember

class PermissionPrecondition(private val permissionsService: PermissionsService) : Precondition() {
    override fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val requiredPermissionLevel = event.command?.requiredPermissionLevel ?: DEFAULT_REQUIRED_PERMISSION
        val guild = event.guild!!
        val member = event.author.toMember(guild)!!

        if (!permissionsService.hasClearance(member, requiredPermissionLevel))
            return Fail(Locale.FAIL_MISSING_CLEARANCE)

        return Pass
    }

}