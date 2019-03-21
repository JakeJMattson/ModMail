package me.aberrantfox.warmbot.mocks

import io.mockk.*
import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.warmbot.mocks.jda.*

fun makeCommandEventMock(vararg _args: Any) = mockk<CommandEvent>(relaxed = true) {
    every { args } returns _args.toList()
    every { jda } returns jdaMock
    every { message } returns produceMessageMock()
}