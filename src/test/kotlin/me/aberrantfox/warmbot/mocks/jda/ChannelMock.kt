package me.aberrantfox.warmbot.mocks.jda

import io.mockk.*
import me.aberrantfox.warmbot.mocks.TestConstants
import net.dv8tion.jda.api.entities.*

fun produceTextChannelMock(_guild: Guild) = mockk<TextChannel>(relaxed = true) {
    every { guild } returns _guild
    every { id } returns TestConstants.Channel_ID
}

fun produceVoiceChannelMock(_guild: Guild) = mockk<VoiceChannel>(relaxed = true) {
    every { guild } returns _guild
    every { id } returns TestConstants.Channel_ID
}