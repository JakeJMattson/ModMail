package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.warmbot.services.findReport
import me.jakejmattson.kutils.api.dsl.command.CommandEvent
import me.jakejmattson.kutils.api.dsl.preconditions.*

class ReportPrecondition : Precondition() {
    override fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val command = event.command ?: return Pass

        if (command.category != "Report") return Pass

        event.channel.findReport() ?: return Fail("This command must be invoked inside a report.")

        return Pass
    }
}