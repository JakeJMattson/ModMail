# Commands

## Key
| Symbol     | Meaning                    |
| ---------- | -------------------------- |
| (Argument) | This argument is optional. |

## Configuration
| Commands          | Arguments    | Description                                                           |
| ----------------- | ------------ | --------------------------------------------------------------------- |
| SetArchiveChannel | Text Channel | Set the channel where transcribed reports will be sent when archived. |
| SetLoggingChannel | Text Channel | Set the channel where events will be logged.                          |
| SetReportCategory | Category     | Set the category where new reports will be opened.                    |
| SetStaffRole      | Any          | Specify the role required to use this bot.                            |

## Macros
| Commands    | Arguments                 | Description                                                                           |
| ----------- | ------------------------- | ------------------------------------------------------------------------------------- |
| AddMacro    | Macro Name, Macro Content | Add a macro which will respond with the given message when invoked by the given name. |
| EditMacro   | Macro, New Message        | Change a macro's response message.                                                    |
| ListMacros  | <none>                    | List all of the currently available map.                                              |
| RemoveMacro | Macro                     | Removes a macro with the given name.                                                  |
| RenameMacro | Macro, New Name           | Change a macro's name, keeping the original response.                                 |

## Owner
| Commands    | Arguments                                      | Description                          |
| ----------- | ---------------------------------------------- | ------------------------------------ |
| SetPrefix   | Prefix                                         | Set the bot's prefix.                |
| SetPresence | (Playing/Watching/Listening), Presence Message | Set the Discord presence of the bot. |

## Report
| Commands  | Arguments                | Description                                                                                  |
| --------- | ------------------------ | -------------------------------------------------------------------------------------------- |
| Archive   | (Report Channel), (Info) | Archive the contents of the report as a text document in the archive channel.                |
| Close     | (Report Channel)         | Close the report channel that this command is invoked in. Alternatively, delete the channel. |
| Note      | (Report Channel), Note   | Produce a note in a report channel in the form of an embed.                                  |
| ResetTags | (Report Channel)         | Reset a report channel to its original name.                                                 |
| Tag       | (Report Channel), Tag    | Prepend a tag to the name of this report channel.                                            |

## ReportHelpers
| Commands             | Arguments                 | Description                                                                                       |
| -------------------- | ------------------------- | ------------------------------------------------------------------------------------------------- |
| Detain               | Member, (Initial Message) | Mute a user and open a report with them.                                                          |
| History, PeekHistory | User                      | Read the target user's DM history with the bot.                                                   |
| Info, ReportInfo     | (Report Channel), (Field) | Retrieve the requested id info from the target report channel. Fields: user, channel, guild, all. |
| Open                 | Member, (Initial Message) | Open a report with the target user and send the provided initial message.                         |
| Release              | (Report Channel), Member  | Release a user from detainment and unmute them.                                                   |

## Utility
| Commands             | Arguments | Description                              |
| -------------------- | --------- | ---------------------------------------- |
| Help                 | (Command) | Display a help menu.                     |
| Status, Ping, Uptime | <none>    | Display network status and total uptime. |

