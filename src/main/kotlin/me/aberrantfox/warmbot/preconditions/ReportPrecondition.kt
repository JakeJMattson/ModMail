package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.services.ReportService

private const val Category = "report"

@Precondition
fun produceIsReportPrecondition(reportService: ReportService) = exit@{ event: CommandEvent ->
    val command = event.container.commands[event.commandStruct.commandName] ?: return@exit Pass

    if(command.category != Category) return@exit Pass

    if (!reportService.isReportChannel(event.channel.id))
        return@exit Fail("This command must be invoked inside a report.")

Pass
}

