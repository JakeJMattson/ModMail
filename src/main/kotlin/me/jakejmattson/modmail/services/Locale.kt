package me.jakejmattson.modmail.services

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

val Locale = run {
    val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        encodeDefaults = true
        serializersModule = SerializersModule { }
    }

    val messages = if (messagesFile.exists()) json.decodeFromString(messagesFile.readText()) else Messages()
    messagesFile.writeText(json.encodeToString(messages))
    messages
}

@Serializable
class Messages(
    //User-facing
    val DISCORD_PRESENCE: String = "DM to contact Staff",
    val BOT_DESCRIPTION: String = "This is a two-way communication medium between you and the entire staff team. Reply directly into this channel and your message will be forwarded to them.",
    val USER_DETAIN_MESSAGE: String = "You have been muted during this detainment period. Please use this time to converse with us. Send messages here to reply.",

    //Configuration commands descriptions
    val CONFIGURE_DESCRIPTION: String = "Configure the bot channels and settings.",
    val REPORT_CATEGORY_DESCRIPTION: String = "Set the category where new reports will be opened.",
    val ARCHIVE_CHANNEL_DESCRIPTION: String = "Set the channel where reports will be sent when archived.",
    val LOGGING_CHANNEL_DESCRIPTION: String = "Set the channel where events will be logged.",

    //Report commands descriptions
    val CLOSE_DESCRIPTION: String = "Delete a report channel and end this report.",
    val ARCHIVE_DESCRIPTION: String = "Archive the contents of this report as text.",
    val NOTE_DESCRIPTION: String = "Add an embed note in this report channel.",
    val TAG_DESCRIPTION: String = "Prepend a tag to the name of this report channel.",
    val RESET_TAGS_DESCRIPTION: String = "Reset a report channel to its original name.",

    //Report helper commands descriptions
    val OPEN_DESCRIPTION: String = "Open a report with the target user.",
    val DETAIN_DESCRIPTION: String = "Mute a user and open a report with them.",
    val RELEASE_DESCRIPTION: String = "Release a user from detainment and unmute them.",
    val INFO_DESCRIPTION: String = "Retrieve info from the target report channel - user/channel/all.",
    val HISTORY_DESCRIPTION: String = "Read the target user's DM history with the bot.",

    //Macro commands descriptions
    val SEND_MACRO_DESCRIPTION: String = "Send a macro to a user through a report.",
    val ADD_MACRO_DESCRIPTION: String = "Add a custom command to send text in a report.",
    val REMOVE_MACRO_DESCRIPTION: String = "Removes a macro with the given name.",
    val RENAME_MACRO_DESCRIPTION: String = "Change a macro's name, keeping the original response.",
    val EDIT_MACRO_DESCRIPTION: String = "Change a macro's response message.",
    val LIST_MACROS_DESCRIPTION: String = "List all of the currently available macros.",

    //Fail message
    val FAIL_GUILD_NOT_CONFIGURED: String = "This guild is not configured for use.",
)
