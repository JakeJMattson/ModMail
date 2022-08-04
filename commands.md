# Commands

## Key 
| Symbol      | Meaning                        |
|-------------|--------------------------------|
| [Argument]  | Argument is not required.      |
| /Category   | This is a subcommand group.    |

## /Macro
| Commands | Arguments      | Description                                           |
|----------|----------------|-------------------------------------------------------|
| Add      | Name, Content  | Add a custom command to send text in a report.        |
| Edit     | Macro, Content | Change a macro's response message.                    |
| List     |                | List all of the currently available macros.           |
| Remove   | Macro          | Removes a macro with the given name.                  |
| Rename   | Macro, NewName | Change a macro's name, keeping the original response. |
| Send     | Macro          | Send a macro to a user through a report.              |

## Configuration
| Commands       | Arguments                                      | Description                                               |
|----------------|------------------------------------------------|-----------------------------------------------------------|
| ArchiveChannel | Channel                                        | Set the channel where reports will be sent when archived. |
| Configure      | ReportCategory, ArchiveChannel, LoggingChannel | Configure the bot channels and settings.                  |
| LoggingChannel | Channel                                        | Set the channel where events will be logged.              |
| ReportCategory | Category                                       | Set the category where new reports will be opened.        |

## Report
| Commands  | Arguments | Description                                       |
|-----------|-----------|---------------------------------------------------|
| Archive   | [Info]    | Archive the contents of this report as text.      |
| Close     |           | Delete a report channel and end this report.      |
| Note      | Note      | Add an embed note in this report channel.         |
| ResetTags |           | Reset a report channel to its original name.      |
| Tag       | Tag       | Prepend a tag to the name of this report channel. |

## ReportHelpers
| Commands | Arguments | Description                                                      |
|----------|-----------|------------------------------------------------------------------|
| Detain   | User      | Mute a user and open a report with them.                         |
| History  | User      | Read the target user's DM history with the bot.                  |
| Info     | [Field]   | Retrieve info from the target report channel - user/channel/all. |
| Open     | User      | Open a report with the target user.                              |
| Release  |           | Release a user from detainment and unmute them.                  |

## Utility
| Commands | Arguments | Description          |
|----------|-----------|----------------------|
| Help     | [Command] | Display a help menu. |

