package me.aberrantfox.warmbot.messages

class Messages(
    //General Descriptions
    val BOT_DESCRIPTION: String = "",

    //Configuration commands descriptions
    val SET_REPORT_CATEGORY_DESCRIPTION: String = "",
    val SET_ARCHIVE_CHANNEL_DESCRIPTION: String = "",
    val SET_STAFF_ROLE_DESCRIPTION: String = "",
    val SET_LOGGING_CHANNEL_DESCRIPTION: String = "",
    val ADD_STAFF_CHANNEL_DESCRIPTION: String = "",
    val REMOVE_STAFF_CHANNEL_DESCRIPTION: String = "",
    val LIST_STAFF_CHANNELS_DESCRIPTION: String = "",

    //Owner commands descriptions
    val SET_PRESENCE_DESCRIPTION: String = "",
    val CLOSE_DESCRIPTION: String = "",
    val ARCHIVE_DESCRIPTION: String = "",
    val NOTE_DESCRIPTION: String = "",

    //Report commands descriptions
    val MOVE_DESCRIPTION: String = "",
    val TAG_DESCRIPTION: String = "",
    val RESET_TAGS_DESCRIPTION: String = "",
    val OPEN_DESCRIPTION: String = "",
    val DETAIN_DESCRIPTION: String = "",
    val USER_DETAIN_MESSAGE: String = "",

    //Report helper commands descriptions
    val RELEASE_DESCRIPTION: String = "",
    val CLOSE_ALL_DESCRIPTION: String = "",
    val INFO_DESCRIPTION: String = "",
    val IS_REPORT_DESCRIPTION: String = "",
    val PEEK_HISTORY_DESCRIPTION: String = "",

    //Info commands descriptions
    val SEND_MACRO_DESCRIPTION: String = "",
    val ADD_MACRO_DESCRIPTION: String = "",
    val REMOVE_MACRO_DESCRIPTION: String = "",

    //Macro commands descriptions
    val RENAME_MACRO_DESCRIPTION: String = "",
    val EDIT_MACRO_DESCRIPTION: String = "",
    val LIST_MACROS_DESCRIPTION: String = "",
    val STATUS_DESCRIPTION: String = "Display network status and total uptime.",
    val SET_REPORT_CATEGORY_SUCCESSFUL: String = "",
    val SET_ARCHIVE_CHANNEL_SUCCESSFUL: String = "",

    //Utility commands descriptions
    val SET_STAFF_ROLE_SUCCESSFUL: String = "",

    //Success message
    val SET_LOGGING_CHANNEL_SUCCESSFUL: String = "",
    val GUILD_SETUP_SUCCESSFUL: String = "",
    val FAIL_GUILD_SETUP: String = "",
    val FAIL_MISSING_CLEARANCE: String = "",
    val FAIL_COULD_NOT_FIND_ROLE: String = "",

    //Fail message
    val FAIL_GUILD_NOT_CONFIGURED: String = "",
    val STARTUP_LOG: String = "",
    val MEMBER_OPEN_LOG: String = "",
    val STAFF_OPEN_LOG: String = "",
    val ARCHIVE_LOG: String = "",

    //Logging messages
    val COMMAND_CLOSE_LOG: String = "",
    val MANUAL_CLOSE_LOG: String = "",
    val COMMAND_LOG: String = "",
    val ERROR_LOG: String = "",
    val DEFAULT_REPORT_CATEGORY_NAME: String = "",
    val DEFAULT_HOLDER_CATEGORY_NAME: String = "",
    val DEFAULT_ARCHIVE_CHANNEL_NAME: String = "",
    val DEFAULT_LOGGING_CHANNEL_NAME: String = "",

    //Default setup info
    val DEFAULT_COMMAND_CHANNEL_NAME: String = "",
    val DEFAULT_STAFF_ROLE_NAME: String = "",
    val DEFAULT_INITIAL_MESSAGE: String = "",
    val DEFAULT_DISCORD_PRESENCE: String = ""
)
