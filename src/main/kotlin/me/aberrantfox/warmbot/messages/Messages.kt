package me.aberrantfox.warmbot.messages

class Messages (
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
    val WHITELIST_DESCRIPTION: String = "",
    val UNWHITELIST_DESCRIPTION: String = "",
    val SHOW_WHITELIST_DESCRIPTION: String = "",
    val SET_PRESENCE_DESCRIPTION: String = "",

    //Report commands descriptions
    val CLOSE_DESCRIPTION: String = "",
    val ARCHIVE_DESCRIPTION: String = "",
    val NOTE_DESCRIPTION: String = "",
    val MOVE_DESCRIPTION: String = "",
    val TAG_DESCRIPTION: String = "",
    val RESET_TAGS_DESCRIPTION: String = "",

    //Report helper commands descriptions
    val OPEN_DESCRIPTION: String = "",
    val DETAIN_DESCRIPTION: String = "",
    val USER_DETAIN_MESSAGE: String = "",
    val RELEASE_DESCRIPTION: String = "",
    val CLOSE_ALL_DESCRIPTION: String = "",
    val INFO_DESCRIPTION: String = "",

    //Macro commands descriptions
    val SEND_MACRO_DESCRIPTION: String = "",
    val ADD_MACRO_DESCRIPTION: String = "",
    val REMOVE_MACRO_DESCRIPTION: String = "",
    val RENAME_MACRO_DESCRIPTION: String = "",
    val EDIT_MACRO_DESCRIPTION: String = "",
    val LIST_MACROS_DESCRIPTION: String = "",

    //Utility commands descriptions
    val PING_DESCRIPTION: String = "",
    val VERSION_DESCRIPTION: String = "",
    val AUTHOR_DESCRIPTION: String = "",
    val SOURCE_DESCRIPTION: String = "",
    val BOT_INFO_DESCRIPTION: String = "",
    val UPTIME_DESCRIPTION: String = "",

    //Success message
    val SET_REPORT_CATEGORY_SUCCESSFUL: String = "",
    val SET_ARCHIVE_CHANNEL_SUCCESSFUL: String = "",
    val SET_STAFF_ROLE_SUCCESSFUL: String = "",
    val SET_LOGGING_CHANNEL_SUCCESSFUL: String = "",
    val GUILD_SETUP_SUCCESSFUL: String = "",

    //Fail message
    val FAIL_GUILD_SETUP: String = "",
    val FAIL_MUST_BE_BOT_OWNER: String = "",
    val FAIL_MUST_BE_GUILD_OWNER: String = "",
    val FAIL_TEXT_CHANNEL_ONLY: String = "",
    val FAIL_COMMAND_NOT_IN_GUILD: String = "",
    val FAIL_COULD_NOT_FIND_ROLE: String = "",
    val FAIL_MISSING_STAFF_ROLE: String = "",
    val FAIL_GUILD_NOT_WHITELISTED: String = "",
    val FAIL_GUILD_NOT_CONFIGURED: String = "",

    //Logging messages
    val STARTUP_LOG: String = "",
    val MEMBER_OPEN_LOG: String = "",
    val STAFF_OPEN_LOG: String = "",
    val ARCHIVE_LOG: String = "",
    val CLOSE_LOG: String = "",
    val COMMAND_LOG: String = "",

    //Default setup info
    val DEFAULT_REPORT_CATEGORY_NAME: String = "",
    val DEFAULT_HOLDER_CATEGORY_NAME: String = "",
    val DEFAULT_ARCHIVE_CHANNEL_NAME: String = "",
    val DEFAULT_LOGGING_CHANNEL_NAME: String = "",
    val DEFAULT_COMMAND_CHANNEL_NAME: String = "",
    val DEFAULT_STAFF_ROLE_NAME: String = "",

    //Default text
    val DEFAULT_INITIAL_MESSAGE: String = "",
    val DEFAULT_DISCORD_PRESENCE: String = ""
)
