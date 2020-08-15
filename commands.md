# Commands

## Key
| Symbol     | Meaning                    |
| ---------- | -------------------------- |
| (Argument) | This argument is optional. |

## Configuration
| Commands          | Arguments    | Description                                               |
| ----------------- | ------------ | --------------------------------------------------------- |
| SetArchiveChannel | Text Channel | Set the channel where reports will be sent when archived. |
| SetLoggingChannel | Text Channel | Set the channel where events will be logged.              |
| SetReportCategory | Category     | Set the category where new reports will be opened.        |
| SetStaffRole      | Role         | Specify the role required to use this bot.                |

## Macros
| Commands    | Arguments                 | Description                                           |
| ----------- | ------------------------- | ----------------------------------------------------- |
| AddMacro    | Macro Name, Macro Content | Add a custom command to send text in a report.        |
| EditMacro   | Macro, New Message        | Change a macro's response message.                    |
| ListMacros  |                           | List all of the currently available macros.           |
| RemoveMacro | Macro                     | Removes a macro with the given name.                  |
| RenameMacro | Macro, New Name           | Change a macro's name, keeping the original response. |

## Owner
| Commands    | Arguments                                      | Description                          |
| ----------- | ---------------------------------------------- | ------------------------------------ |
| SetPrefix   | Prefix                                         | Set the bot's prefix.                |
| SetPresence | (Playing/Watching/Listening), Presence Message | Set the Discord presence of the bot. |

## Report
| Commands  | Arguments                | Description                                       |
| --------- | ------------------------ | ------------------------------------------------- |
| Archive   | (Report Channel), (Info) | Archive the contents of this report as text.      |
| Close     | (Report Channel)         | Delete a report channel and end this report.      |
| Note      | (Report Channel), Note   | Add an embed note in this report channel.         |
| ResetTags | (Report Channel)         | Reset a report channel to its original name.      |
| Tag       | (Report Channel), Tag    | Prepend a tag to the name of this report channel. |

## ReportHelpers
| Commands | Arguments                 | Description                                                               |
| -------- | ------------------------- | ------------------------------------------------------------------------- |
| Detain   | Member, (Initial Message) | Mute a user and open a report with them.                                  |
| History  | User                      | Read the target user's DM history with the bot.                           |
| Info     | (Report Channel), (Field) | Retrieve info from the target report channel - user/channel/all.          |
| Open     | Member, (Initial Message) | Open a report with the target user and send the provided initial message. |
| Release  | (Report Channel), Member  | Release a user from detainment and unmute them.                           |

## Utility
| Commands     | Arguments | Description                              |
| ------------ | --------- | ---------------------------------------- |
| Help         | (Command) | Display a help menu.                     |
| Status, Ping |           | Display network status and total uptime. |

