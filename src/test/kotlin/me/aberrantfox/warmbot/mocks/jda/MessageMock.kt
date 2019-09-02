package me.aberrantfox.warmbot.mocks.jda

import io.mockk.*
import me.aberrantfox.warmbot.mocks.TestConstants
import net.dv8tion.jda.api.entities.Message

fun produceMessageMock() = mockk<Message>(relaxed = true) {
    every { author } returns produceUserMock()
    every { guild } returns guildMock
    every { id } returns TestConstants.Message_ID
}