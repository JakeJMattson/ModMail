package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.command.*
import me.aberrantfox.warmbot.services.isReportChannel

private const val Category = "report"

@Precondition
fun produceIsReportPrecondition() = exit@{ event: CommandEvent ->
    val command = event.container.commands[event.commandStruct.commandName] ?: return@exit Pass

    if (command.category != Category) return@exit Pass

    if (!event.channel.isReportChannel()) return@exit Fail("This command must be invoked inside a report.")

    return@exit Pass
}

