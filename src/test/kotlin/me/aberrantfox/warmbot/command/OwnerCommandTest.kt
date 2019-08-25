package me.aberrantfox.warmbot.command

import io.mockk.*
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.warmbot.commands.ownerCommands
import me.aberrantfox.warmbot.extensions.executeByName
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.mocks.*
import me.aberrantfox.warmbot.mocks.jda.guildMock
import me.aberrantfox.warmbot.services.*
import org.junit.jupiter.api.*

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
        commands = ownerCommands(configuration, prefixServiceMock, guildServiceMock, persistenceServiceMock)
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