package me.jakejmattson.modmail.arguments

import dev.kord.core.Kord
import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.CommandEvent

open class PresenceArg(override val name: String = "Presence") : Argument<Presence> {
    companion object : PresenceArg()

    override val description = "A presence type"

    override suspend fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Presence> {
        val presence = when (arg.lowercase()) {
            "playing" -> Presence.PLAYING
            "watching" -> Presence.WATCHING
            "listening" -> Presence.LISTENING
            else -> null
        } ?: return Error("Unknown presence type!")

        return Success(presence)
    }

    override suspend fun generateExamples(event: CommandEvent<*>) = listOf("Playing/Watching/Listening")
}

private interface PresenceType {
    suspend fun apply(kord: Kord, text: String)
}

enum class Presence : PresenceType {
    PLAYING {
        override suspend fun apply(kord: Kord, text: String) {
            kord.editPresence {
                playing(text)
            }
        }
    },
    WATCHING {
        override suspend fun apply(kord: Kord, text: String) {
            kord.editPresence {
                watching(text)
            }
        }
    },
    LISTENING {
        override suspend fun apply(kord: Kord, text: String) {
            kord.editPresence {
                listening(text)
            }
        }
    }
}