# Commands

## Key
| Symbol     | Meaning                    |
| ---------- | -------------------------- |
| (Argument) | This argument is optional. |

## Configuration
| Commands           | Arguments   | Description                                                                         |
| ------------------ | ----------- | ----------------------------------------------------------------------------------- |
| AddStaffChannel    | TextChannel | Whitelist a channel. The bot will now respond to commands in this channel.          |
| ListStaffChannels  | <none>      | List the whitelisted channels - the channels where the bot will listen to commands. |
| RemoveStaffChannel | TextChannel | Unwhitelist a channel. The bot will no longer respond to commands in this channel.  |
| SetArchiveChannel  | TextChannel | Set the channel where transcribed reports will be sent when archived.               |
| SetLoggingChannel  | TextChannel | Set the channel where events will be logged.                                        |
| SetReportCategory  | Category    | Set the category where new reports will be opened.                                  |
| SetStaffRole       | Word        | Specify the role required to use this bot.                                          |

## Info
| Commands    | Arguments          | Description                                                                                       |
| ----------- | ------------------ | ------------------------------------------------------------------------------------------------- |
| IsReport    | (Channel)          | Check whether or not a channel is a valid report channel.                                         |
| PeekHistory | User               | Read the target user's DM history with the bot.                                                   |
| ReportInfo  | (Channel), (Field) | Retrieve the requested id info from the target report channel. Fields: user, channel, guild, all. |

## Macros
| Commands    | Arguments                 | Description                                                                           |
| ----------- | ------------------------- | ------------------------------------------------------------------------------------- |
| AddMacro    | Macro Name, Macro Content | Add a macro which will respond with the given message when invoked by the given name. |
| EditMacro   | Macro, New Message        | Change a macro's response message.                                                    |
| ListMacros  | <none>                    | List all of the currently available map.                                              |
| RemoveMacro | Macro                     | Removes a macro with the given name.                                                  |
| RenameMacro | Macro, New Name           | Change a macro's name, keeping the original response.                                 |
| SendMacro   | Macro                     | Send a macro's message through a report channel.                                      |

## Owner
| Commands      | Arguments                                      | Description                          |
| ------------- | ---------------------------------------------- | ------------------------------------ |
| SetPrefix     | Prefix                                         | Set the bot's prefix.                |
| SetPresence   | (Playing/Watching/Listening), Presence Message | Set the Discord presence of the bot. |
| ShowWhitelist | <none>                                         | Display all guilds in the whitelist. |
| UnWhitelist   | Guild                                          | Remove a guild from the whitelist.   |
| Whitelist     | Guild                                          | Add a guild to the whitelist.        |

## Report
| Commands  | Arguments                    | Description                                                                                  |
| --------- | ---------------------------- | -------------------------------------------------------------------------------------------- |
| Archive   | (Additional Info)            | Archive the contents of the report as a text document in the archive channel.                |
| Close     | <none>                       | Close the report channel that this command is invoked in. Alternatively, delete the channel. |
| Move      | Category, (Sync Permissions) | Move a report from the current category to another category and sync permissions.            |
| Note      | Text                         | Produce a note in a report channel in the form of an embed.                                  |
| ResetTags | <none>                       | Reset a report channel to its original name.                                                 |
| Tag       | Word or Emote                | Prepend a tag to the name of this report channel.                                            |

## ReportHelpers
| Commands | Arguments                 | Description                                                               |
| -------- | ------------------------- | ------------------------------------------------------------------------- |
| CloseAll | <none>                    | Close all currently open reports. Can be invoked in any channel.          |
| Detain   | Member, (Initial Message) | Mute a user and open a report with them.                                  |
| Open     | Member, (Initial Message) | Open a report with the target user and send the provided initial message. |
| Release  | Member                    | Release a user from detainment and unmute them.                           |

## Utility
| Commands | Arguments | Description                                 |
| -------- | --------- | ------------------------------------------- |
| Help     | (Command) | Display a help menu.                        |
| Ping     | <none>    | Check the status of the bot.                |
| Uptime   | <none>    | Displays how long the bot has been running. |

