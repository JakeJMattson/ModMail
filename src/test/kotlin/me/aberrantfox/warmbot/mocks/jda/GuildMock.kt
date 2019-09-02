package me.aberrantfox.warmbot.mocks.jda

import io.mockk.*
import me.aberrantfox.warmbot.mocks.TestConstants
import net.dv8tion.jda.api.entities.Guild

val guildMock = mockk<Guild>(relaxed = true) {
    every { id } returns TestConstants.Guild_ID
}