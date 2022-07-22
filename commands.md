# Commands

## Key 
| Symbol      | Meaning                        |
|-------------|--------------------------------|
| [Argument]  | Argument is not required.      |

## Configuration
| Commands       | Arguments | Description                                               |
|----------------|-----------|-----------------------------------------------------------|
| ArchiveChannel | Channel   | Set the channel where reports will be sent when archived. |
| LoggingChannel | Channel   | Set the channel where events will be logged.              |
| ReportCategory | Channel   | Set the category where new reports will be opened.        |
| StaffRole      | Role      | Specify the role required to use this bot.                |

## Macros
| Commands    | Arguments      | Description                                           |
|-------------|----------------|-------------------------------------------------------|
| AddMacro    | Name, Content  | Add a custom command to send text in a report.        |
| EditMacro   | Macro, Content | Change a macro's response message.                    |
| ListMacros  |                | List all of the currently available macros.           |
| RemoveMacro | Macro          | Removes a macro with the given name.                  |
| RenameMacro | Macro, NewName | Change a macro's name, keeping the original response. |
| SendMacro   | Macro          | Send a macro to a user through a report.              |

## Report
| Commands  | Arguments             | Description                                       |
|-----------|-----------------------|---------------------------------------------------|
| Archive   | ReportChannel, [Info] | Archive the contents of this report as text.      |
| Close     | ReportChannel         | Delete a report channel and end this report.      |
| Note      | ReportChannel, Note   | Add an embed note in this report channel.         |
| ResetTags | ReportChannel         | Reset a report channel to its original name.      |
| Tag       | ReportChannel, Tag    | Prepend a tag to the name of this report channel. |

## ReportHelpers
| Commands | Arguments              | Description                                                      |
|----------|------------------------|------------------------------------------------------------------|
| Detain   | User                   | Mute a user and open a report with them.                         |
| History  | User                   | Read the target user's DM history with the bot.                  |
| Info     | ReportChannel, [Field] | Retrieve info from the target report channel - user/channel/all. |
| Open     | User                   | Open a report with the target user.                              |
| Release  | ReportChannel          | Release a user from detainment and unmute them.                  |

## Utility
| Commands | Arguments | Description          |
|----------|-----------|----------------------|
| Help     | [Command] | Display a help menu. |

