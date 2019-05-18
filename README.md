# WarmBot - A Report Management Discord Bot

WarmBot is a report management bot designed to provide a communication system between server staff and other members.
In an ordinary server, users typically need to ping or message a staff member in order to get an issue resolved.
This can create complications, such as the staff member not being online, not knowing the answer, being too busy to respond, or countless other issues.
Other staff may be a perfect fit for the issue, but the member has no way of knowing who to message or ping.
This can lead to passing around screenshots of conversations, information duplication, and in short, a big mess.
WarmBot fixes this.

![WarmBot](https://i.imgur.com/EJEC0Eu.png)

## Reports
Reports are private text channels that allow the entire staff team to communicate with a single member.
You can have as many of these as you want (limited by configuration and Discord).

![Reports](https://i.imgur.com/7vgwc9E.png)

## Member Experience
### Opening a Report
To open a report as a member, simply message the bot.
A new report will be opened automatically.

![Member](https://i.imgur.com/tf9k6PI.png)

### Using a Report
Once a report is opened, you can respond to the bot as if it were any other conversation.
All messages sent to the bot will be forwarded to your report channel for staff to see.

## Staff Experience
### Opening a Report
If a report needs to be opened from the staff side, you can use the `open` command.
This will attempt to create a new report with the target user.
This can be useful if you see a member misbehaving on the server but don't want to reprimand them publicly.

### Using a Report
Once a report is opened, anyone with access to this private channel can talk with the user through the bot.
Talking through WarmBot has the added benefit of masking staff identities.
If things become hostile, the user will have no idea who they're talking to.

![Staff](https://i.imgur.com/tRLMPwj.png)

#### Closing a Report
##### From Discord
 * Delete the channel - WarmBot will detect the event and close the report for you.

##### Using Commands
 * In the report channel, `close` - This has the same effect as deleting the channel.
 * In the report channel, `archive` - Transcribes the report to text, archives it, then closes the report.

## Setup
Refer to [warmbotsetup.md](warmbotsetup.md) for full setup instructions.

## Commands
Below you can find a general list and explanation of all available commands.
To learn about commands during runtime, use the `help` command!

### Owner

`These commands can only be run by the owner of the bot.`

| Command       | Arguments | Effect                                |
| ------        | ------    | ------                                |
| Whitelist     | Guild ID  | Add a guild to the whitelist.         |
| UnWhitelist   | Guild ID  | Remove a guild from the whitelist.    |
| ShowWhitelist | (none)    | Display all guilds in the whitelist.  |
| SetPresence   | Presence  | Set the Discord presence of the bot.  |

### Configuration

`These commands can only be run by the owner of the guild.`

| Command           | Arguments     | Effect                                            |
| ------            | ------        | ------                                            |
| SetReportCategory | Category ID   | Set the category where new reports will be opened.|
| SetArchiveChannel | Text Channel  | Set the channel where reports will be archived.   |
| SetStaffRole      | Role name     | Set the role required to use this bot.            |
| SetLoggingChannel | Text Channel  | Set the channel where events should be logged.    |
| AddStaffChannel   | Text Channel  | Whitelist. Listen to commands in this channel.    |
| RemoveStaffChannel| Text Channel  | Unwhitelist. Ignore commands in this channel.     |
| ListStaffChannels | (none)        | List the whitelisted channels.                    |

### Report

`These commands can only be run in a report channel.`

| Command   | Arguments   | Effect                                    |
| ------    | ------      | ------                                    |
| Close     | (none)      | Close report and notify user.             |
| Archive   | (none)      | Transcribe report to text (closes report).|
| Note      | (none)      | Produce a note in the form of an embed.   |
| Move      | Category ID | Move this channel to another category.    |
| Tag       | Word / Emote| Prepend a tag to this report channel.     |
| ResetTags | (none)      | Remove all tags from a report channel.    |

### Report Helpers

| Command   | Arguments    | Effect                                        |
| ------    | ------       | ------                                        |
| Open      | User         | Open a report with the target user.           |
| Detain    | Member       | Mute a user and open a report with them.      |
| Release   | Member       | Release a user from detainment - unmute them. |
| CloseAll  | (none)       | Close all reports in the current guild.       |
| Info      | Text Channel | Get info (ID's) from the target report.       |

### Macros

| Command     | Arguments       | Effect                                            |
| ------      | ------          | ------                                            |
| SendMacro   | Macro           | Send a macro's message through a report channel.  |
| AddMacro    | Word, Sentence  | Add a macro with a name and its response.         |
| RemoveMacro | Macro           | Removes a macro with the given name.              |
| RenameMacro | Macro, Word     | Change a macro's name.                            | 
| EditMacro   | Macro, Sentence | Change a macro's response.                        |
| ListMacros  | (none)          | List all of the currently available macros.       |

### Utility

| Command      | Arguments | Effect                                    |
| ------       | ------    | ------                                    |
| Author       | (none)    | Display the author of the bot.            |
| Ping         | (none)    | Display the status of the bot.            |
| Source       | (none)    | Display the source code via a GitLab link.|
| Version      | (none)    | Display the current running version.      |
| BotInfo      | (none)    | Display a summary or bot information.     |
| Uptime       | (none)    | Display the amount of time online.        |
| ListCommands | (none)    | List all available commands.              |
