package me.aberrantfox.warmbot.command

import io.mockk.*
import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.warmbot.commands.configurationCommands
import me.aberrantfox.warmbot.extensions.executeByName
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.mocks.*
import me.aberrantfox.warmbot.mocks.jda.*
import me.aberrantfox.warmbot.services.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.*

class ConfigurationCommandsTest {
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

        @AfterAll
        fun cleanUp() {
            unmockkAll()
        }

        @JvmStatic
        fun arguments() = listOf(
            Arguments.of("SetReportCategory", TestConstants.Category_ID,
                { guildConfig: GuildConfiguration -> guildConfig.reportCategory }, produceCategoryMock(guildMock)),
            Arguments.of("SetArchiveChannel", TestConstants.Channel_ID,
                { guildConfig: GuildConfiguration -> guildConfig.archiveChannel }, produceTextChannelMock(guildMock)),
            Arguments.of("SetLoggingChannel", TestConstants.Channel_ID,
                { guildConfig: GuildConfiguration -> guildConfig.loggingConfiguration.loggingChannel }, produceTextChannelMock(guildMock)),
            Arguments.of("SetStaffRole", TestConstants.Staff_Role,
                { guildConfig: GuildConfiguration -> guildConfig.staffRoleName }, TestConstants.Staff_Role)
        )
    }

    private lateinit var config: Configuration
    private lateinit var configurationCommandSet: CommandsContainer

    @BeforeEach
    fun setup() {
        config = Configuration()
        configurationCommandSet = configurationCommands(config, persistenceServiceMock)
    }

    @ParameterizedTest
    @MethodSource("arguments")
    fun `test configuration commands`(commandName: String, actual: Any, expected: (config: GuildConfiguration) -> String, eventMockArgs: Any) {
        val event = makeCommandEventMock(eventMockArgs)
        configurationCommandSet.executeByName(commandName, event)

        val guildConfig = config.guildConfigurations.first()
        Assertions.assertEquals(actual, expected.invoke(guildConfig))
        verifySingleResponse(event)
    }

    private fun verifySingleResponse(event: CommandEvent) {
        verify(exactly = 1) {
            persistenceServiceMock.save(config)
            event.respond(any() as String)
        }
    }
}