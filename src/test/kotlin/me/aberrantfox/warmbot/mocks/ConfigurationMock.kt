package me.aberrantfox.warmbot.mocks

import io.mockk.*
import me.aberrantfox.warmbot.services.*

val guildConfiguration = mockk<GuildConfiguration> {
    every { guildId } returns TestConstants.Guild_ID
    every { reportCategory } returns TestConstants.Category_ID
    every { archiveChannel } returns TestConstants.Channel_ID
    every { staffRoleName } returns TestConstants.Staff_Role
}

fun makeConfigurationMock() = mockk<Configuration> {
    every { guildConfigurations } returns arrayListOf(guildConfiguration)
}

