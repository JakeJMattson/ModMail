package me.aberrantfox.warmbot.mocks

import io.mockk.every
import io.mockk.mockk
import me.aberrantfox.warmbot.services.Configuration
import me.aberrantfox.warmbot.services.GuildConfiguration

val guildConfiguration = mockk<GuildConfiguration> {
    every { guildId } returns TestConstants.Guild_ID
    every { reportCategory } returns TestConstants.Category_ID
}

fun makeConfigurationMock() = mockk<Configuration> {
    every { guildConfigurations } returns arrayListOf(guildConfiguration)
}

