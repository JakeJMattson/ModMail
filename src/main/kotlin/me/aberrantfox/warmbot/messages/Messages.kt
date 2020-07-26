package me.aberrantfox.warmbot.messages

class Messages(
    //Configuration commands descriptions
    val SET_REPORT_CATEGORY_DESCRIPTION: String = "",
    val SET_ARCHIVE_CHANNEL_DESCRIPTION: String = "",
    val SET_STAFF_ROLE_DESCRIPTION: String = "",
    val SET_LOGGING_CHANNEL_DESCRIPTION: String = "",

    //Owner commands descriptions
    val SET_PRESENCE_DESCRIPTION: String = "",

    //Report commands descriptions
    val CLOSE_DESCRIPTION: String = "",
    val ARCHIVE_DESCRIPTION: String = "",
    val NOTE_DESCRIPTION: String = "",
    val TAG_DESCRIPTION: String = "",
    val RESET_TAGS_DESCRIPTION: String = "",

    //Report helper commands descriptions
    val OPEN_DESCRIPTION: String = "",
    val DETAIN_DESCRIPTION: String = "",
    val RELEASE_DESCRIPTION: String = "",
    val INFO_DESCRIPTION: String = "",
    val PEEK_HISTORY_DESCRIPTION: String = "",

    //Macro commands descriptions
    val ADD_MACRO_DESCRIPTION: String = "",
    val REMOVE_MACRO_DESCRIPTION: String = "",
    val RENAME_MACRO_DESCRIPTION: String = "",
    val EDIT_MACRO_DESCRIPTION: String = "",
    val LIST_MACROS_DESCRIPTION: String = "",

    //Utility commands descriptions
    val STATUS_DESCRIPTION: String = "",

    //Success message
    val SET_REPORT_CATEGORY_SUCCESSFUL: String = "",
    val SET_ARCHIVE_CHANNEL_SUCCESSFUL: String = "",
    val SET_LOGGING_CHANNEL_SUCCESSFUL: String = "",
    val SET_STAFF_ROLE_SUCCESSFUL: String = "",
    val GUILD_SETUP_SUCCESSFUL: String = "",

    //Fail message
    val FAIL_GUILD_SETUP: String = "",
    val FAIL_MISSING_CLEARANCE: String = "",
    val FAIL_COULD_NOT_FIND_ROLE: String = "",
    val FAIL_GUILD_NOT_CONFIGURED: String = "",

    //Logging messages
    val STARTUP_LOG: String = "",
    val MEMBER_OPEN_LOG: String = "",
    val STAFF_OPEN_LOG: String = "",
    val ARCHIVE_LOG: String = "",
    val COMMAND_CLOSE_LOG: String = "",
    val MANUAL_CLOSE_LOG: String = "",
    val COMMAND_LOG: String = "",
    val ERROR_LOG: String = "",

    //Default setup info
    val DEFAULT_REPORT_CATEGORY_NAME: String = "",
    val DEFAULT_HOLDER_CATEGORY_NAME: String = "",
    val DEFAULT_ARCHIVE_CHANNEL_NAME: String = "",
    val DEFAULT_LOGGING_CHANNEL_NAME: String = "",
    val DEFAULT_STAFF_ROLE_NAME: String = "",

    //Misc
    val DEFAULT_DISCORD_PRESENCE: String = "",
    val BOT_DESCRIPTION: String = "",
    val DEFAULT_INITIAL_MESSAGE: String = "",
    val USER_DETAIN_MESSAGE: String = ""
)
