package me.aberrantfox.warmbot.mocks

import io.mockk.mockk
import me.aberrantfox.warmbot.services.GuildService


val guildServiceMock = mockk<GuildService>(relaxed = true)