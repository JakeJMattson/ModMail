package me.aberrantfox.warmbot.mocks.jda

import io.mockk.every
import io.mockk.mockk
import me.aberrantfox.warmbot.mocks.TestConstants
import net.dv8tion.jda.core.entities.Guild

val guildMock = mockk<Guild>(relaxed = true) {
    every { id } returns TestConstants.Guild_ID
}