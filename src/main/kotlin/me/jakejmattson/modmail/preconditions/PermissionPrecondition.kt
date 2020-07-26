package me.jakejmattson.modmail.preconditions

import me.jakejmattson.modmail.extensions.requiredPermissionLevel
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*
import me.jakejmattson.kutils.api.dsl.command.CommandEvent
import me.jakejmattson.kutils.api.dsl.preconditions.*
import me.jakejmattson.kutils.api.extensions.jda.toMember

class PermissionPrecondition(private val permissionsService: PermissionsService) : Precondition() {
    override fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val requiredPermissionLevel = event.command?.requiredPermissionLevel ?: DEFAULT_REQUIRED_PERMISSION
        val member = event.author.toMember(event.guild!!)!!

        if (!permissionsService.hasClearance(member, requiredPermissionLevel))
            return Fail(Locale.FAIL_MISSING_CLEARANCE)

        return Pass
    }
}