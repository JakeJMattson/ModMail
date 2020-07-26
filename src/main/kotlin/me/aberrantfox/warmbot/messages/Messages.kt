package me.aberrantfox.warmbot.messages

class Messages(
    //User-facing
    val DISCORD_PRESENCE: String = "DM to contact Staff",
    val BOT_DESCRIPTION: String = "This is a two-way communication medium between you and the entire staff team. Reply directly into this channel and your message will be forwarded to them.",
    val USER_DETAIN_MESSAGE: String = "You have been muted during this detainment period. Please use this time to converse with us. Send messages here to reply.",

    //Configuration commands descriptions
    val SET_REPORT_CATEGORY_DESCRIPTION: String = "Set the category where new reports will be opened.",
    val SET_ARCHIVE_CHANNEL_DESCRIPTION: String = "Set the channel where reports will be sent when archived.",
    val SET_STAFF_ROLE_DESCRIPTION: String = "Specify the role required to use this bot.",
    val SET_LOGGING_CHANNEL_DESCRIPTION: String = "Set the channel where events will be logged.",

    //Owner commands descriptions
    val SET_PRESENCE_DESCRIPTION: String = "Set the Discord presence of the bot.",

    //Report commands descriptions
    val CLOSE_DESCRIPTION: String = "Delete a report channel and end this report.",
    val ARCHIVE_DESCRIPTION: String = "Archive the contents of this report as text.",
    val NOTE_DESCRIPTION: String = "Add an embed note in this report channel.",
    val TAG_DESCRIPTION: String = "Prepend a tag to the name of this report channel.",
    val RESET_TAGS_DESCRIPTION: String = "Reset a report channel to its original name.",

    //Report helper commands descriptions
    val OPEN_DESCRIPTION: String = "Open a report with the target user and send the provided initial message.",
    val DETAIN_DESCRIPTION: String = "Mute a user and open a report with them.",
    val RELEASE_DESCRIPTION: String = "Release a user from detainment and unmute them.",
    val INFO_DESCRIPTION: String = "Retrieve info from the target report channel - user/channel/all.",
    val HISTORY_DESCRIPTION: String = "Read the target user's DM history with the bot.",

    //Macro commands descriptions
    val ADD_MACRO_DESCRIPTION: String = "Add a custom command to send text in a report.",
    val REMOVE_MACRO_DESCRIPTION: String = "Removes a macro with the given name.",
    val RENAME_MACRO_DESCRIPTION: String = "Change a macro's name, keeping the original response.",
    val EDIT_MACRO_DESCRIPTION: String = "Change a macro's response message.",
    val LIST_MACROS_DESCRIPTION: String = "List all of the currently available macros.",

    //Utility commands descriptions
    val STATUS_DESCRIPTION: String = "Display network status and total uptime.",

    //Success message
    val SET_REPORT_CATEGORY_SUCCESSFUL: String = "Successfully set report category to :: \${categoryName}",
    val SET_ARCHIVE_CHANNEL_SUCCESSFUL: String = "Successfully the archive channel to :: \${archiveChannel}",
    val SET_LOGGING_CHANNEL_SUCCESSFUL: String = "Successfully the logging channel to :: \${loggingChannel}",
    val SET_STAFF_ROLE_SUCCESSFUL: String = "\"Successfully set the staff role to :: \${staffRoleName}",
    val GUILD_SETUP_SUCCESSFUL: String = "Successfully configured for use! You can adjust these values at any time.",

    //Fail message
    val FAIL_GUILD_SETUP: String = "**Error** :: The \${field} provided did not belong to the guild you started this conversation in.",
    val FAIL_MISSING_CLEARANCE: String = "Missing clearance to use this command.",
    val FAIL_COULD_NOT_FIND_ROLE: String = "Could not find a role named :: \${staffRoleName}",
    val FAIL_GUILD_NOT_CONFIGURED: String = "This guild is not configured for use.",

    //Logging messages
    val STARTUP_LOG: String = "Bot successfully initialized!",
    val MEMBER_OPEN_LOG: String = "New report opened by \${user}",
    val STAFF_OPEN_LOG: String = "Staff action :: \${staff} opened \${channel}",
    val ARCHIVE_LOG: String = "Staff action :: \${staff} archived \${channel}",
    val COMMAND_CLOSE_LOG: String = "Staff action :: \${staff} closed \${channel}",
    val MANUAL_CLOSE_LOG: String = "Staff action :: \${channel} was deleted. See the server audit log for more information.",
    val COMMAND_LOG: String = "\${author} invoked `\${commandName}` in \${channelName}. \${additionalInfo}",
    val ERROR_LOG: String = "Error :: \${message}",

    //Default setup info
    val DEFAULT_REPORT_CATEGORY_NAME: String = "WarmBot-Reports",
    val DEFAULT_HOLDER_CATEGORY_NAME: String = "WarmBot",
    val DEFAULT_ARCHIVE_CHANNEL_NAME: String = "Archive",
    val DEFAULT_LOGGING_CHANNEL_NAME: String = "Logging",
    val DEFAULT_STAFF_ROLE_NAME: String = "Staff",

    //Misc
    val DEFAULT_INITIAL_MESSAGE: String = "<No initial message provided>"
)
