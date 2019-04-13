package me.aberrantfox.warmbot.command

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import me.aberrantfox.kjdautils.api.dsl.CommandEvent
import me.aberrantfox.kjdautils.api.dsl.CommandsContainer
import me.aberrantfox.warmbot.commands.ownerCommands
import me.aberrantfox.warmbot.extensions.executeByName
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.mocks.*
import me.aberrantfox.warmbot.mocks.jda.guildMock
import me.aberrantfox.warmbot.services.Configuration
import me.aberrantfox.warmbot.services.EnvironmentSettings
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OwnerCommandTest {
    companion object {
        init {
            EnvironmentSettings.IS_TESTING_ENVIRONMENT = true
        }

        @BeforeAll
        fun beforeAll() {
            mockkObject(Locale) {
                every { Locale.messages } returns makeMessagesMock()
            }
        }
    }

    private lateinit var commands: CommandsContainer
    private lateinit var configuration: Configuration
    private lateinit var event: CommandEvent

    @BeforeEach
    fun beforeEach() {
        configuration = Configuration()
        commands = ownerCommands(configuration, guildServiceMock, persistenceServiceMock)
    }

    @Test
    fun `Whitelist command adds unwhitelisted Guilds to the whitelist`() {
        event = makeCommandEventMock(guildMock)
        commands.executeByName("Whitelist", event)

        Assertions.assertTrue(configuration.whitelist.isNotEmpty())
        Assertions.assertEquals(TestConstants.Guild_ID, configuration.whitelist.first())

        verify(exactly = 1) {
            persistenceServiceMock.save(configuration)
        }
    }

    @Test
    fun `Whitelist command rejects already whitelisted Guilds`() {
        event = makeCommandEventMock(guildMock)
        configuration.whitelist.add(TestConstants.Guild_ID)
        commands.executeByName("Whitelist", event)

        Assertions.assertEquals(1, configuration.whitelist.size)
        Assertions.assertEquals(TestConstants.Guild_ID, configuration.whitelist.first())

        verify(exactly = 0) {
            persistenceServiceMock.save(configuration)
        }
    }
}