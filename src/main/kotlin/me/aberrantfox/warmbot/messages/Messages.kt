package me.aberrantfox.warmbot.messages

class Messages (
        //Descriptions
        val BOT_DESCRIPTION: String,
        val SET_REPORT_CATEGORY_DESCRIPTION: String,
        val SET_ARCHIVE_CHANNEL_DESCRIPTION: String,
        val SET_STAFF_ROLE_DESCRIPTION: String,
        val CLOSE_DESCRIPTION: String,
        val ARCHIVE_DESCRIPTION: String,
        val NOTE_DESCRIPTION: String,
        val OPEN_DESCRIPTION: String,
        val CLOSE_ALL_DESCRIPTION: String,

        //Success messages
        val ARCHIVE_CHANNEL_SET_SUCCESSFUL: String,
        val REPORT_ARCHIVE_SUCCESSFUL: String,
        val SET_STAFF_ROLE_SUCCESSFUL: String,
        val GUILD_SETUP_SUCCESSFUL: String,

        //Fail messages
        val FAIL_GUILD_SETUP: String,
        val FAIL_MUST_BE_BOT_OWNER: String,
        val FAIL_MUST_BE_GUILD_OWNER: String,
        val FAIL_TEXT_CHANNEL_ONLY: String,
        val FAIL_COULD_NOT_FIND_ROLE: String,
        val FAIL_MISSING_STAFF_ROLE: String,
        val FAIL_GUILD_NOT_WHITELISTED: String,
        val FAIL_GUILD_NOT_CONFIGURED: String,

        //Logging messages
        val STARTUP_LOG: String,
        val MEMBER_OPEN_LOG: String,
        val STAFF_OPEN_LOG: String,
        val ARCHIVE_LOG: String,
        val CLOSE_LOG: String,

        //Default text
        val DEFAULT_INITIAL_MESSAGE: String
)
