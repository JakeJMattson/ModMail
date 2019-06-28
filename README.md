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

| Commands      | Arguments                                          | Description                          |
| ------------- | -------------------------------------------------- | ------------------------------------ |
| SetPresence   | (Playing \| Watching \| Listening), Presence Message | Set the Discord presence of the bot. |
| ShowWhitelist | <none>                                             | Display all guilds in the whitelist. |
| UnWhitelist   | Guild                                              | Remove a guild from the whitelist.   |
| Whitelist     | Guild                                              | Add a guild to the whitelist.        |

### Configuration

`These commands can only be run by the owner of the guild.`

| Commands           | Arguments       | Description                                                                         |
| ------------------ | --------------- | ----------------------------------------------------------------------------------- |
| AddStaffChannel    | TextChannel     | Whitelist a channel. The bot will now respond to commands in this channel.          |
| ListStaffChannels  | <none>          | List the whitelisted channels - the channels where the bot will listen to commands. |
| RemoveStaffChannel | TextChannel     | Unwhitelist a channel. The bot will no longer respond to commands in this channel.  |
| SetArchiveChannel  | TextChannel     | Set the channel where transcribed reports will be sent when archived.               |
| SetLoggingChannel  | TextChannel     | Set the channel where events will be logged.                                        |
| SetReportCategory  | ChannelCategory | Set the category where new reports will be opened.                                  |
| SetStaffRole       | Word            | Specify the role required to use this bot.                                          |

### Report

`These commands can only be run in a report channel.`

| Commands  | Arguments                    | Description                                                                                  |
| --------- | ---------------------------- | -------------------------------------------------------------------------------------------- |
| Archive   | (Additional Info)            | Archive the contents of the report as a text document in the archive channel.                |
| Close     | <none>                       | Close the report channel that this command is invoked in. Alternatively, delete the channel. |
| Move      | Category, (Sync Permissions) | Move a report from the current category to another category and sync permissions.            |
| Note      | Text                         | Produce a note in a report channel in the form of an embed.                                  |
| ResetTags | <none>                       | Reset a report channel to its original name.                                                 |
| Tag       | Word or Emote                | Prepend a tag to the name of this report channel.                                            |

### Report Helpers

| Commands | Arguments                 | Description                                                               |
| -------- | ------------------------- | ------------------------------------------------------------------------- |
| CloseAll | <none>                    | Close all currently open reports. Can be invoked in any channel.          |
| Detain   | Member, (Initial Message) | Mute a user and open a report with them.                                  |
| Open     | Member, (Initial Message) | Open a report with the target user and send the provided initial message. |
| Release  | Member                    | Release a user from detainment and unmute them.                           |

### Info

| Commands    | Arguments                 | Description                                                                                       |
| ----------- | ------------------------- | ------------------------------------------------------------------------------------------------- |
| IsReport    | (Channel)                 | Check whether or not a channel is a valid report channel.                                         |
| PeekHistory | DiscordUser               | Read the target user's DM history with the bot.                                                   |
| ReportInfo  | (Report Channel), (Field) | Retrieve the requested id info from the target report channel. Fields: user, channel, guild, all. |

### Macros

| Commands    | Arguments                 | Description                                                                           |
| ----------- | ------------------------- | ------------------------------------------------------------------------------------- |
| AddMacro    | Macro Name, Macro Content | Add a macro which will respond with the given message when invoked by the given name. |
| EditMacro   | Macro, New Message        | Change a macro's response message.                                                    |
| ListMacros  | <none>                    | List all of the currently available map.                                              |
| RemoveMacro | Macro                     | Removes a macro with the given name.                                                  |
| RenameMacro | Macro, New Name           | Change a macro's name, keeping the original response.                                 |
| SendMacro   | Macro                     | Send a macro's message through a report channel.                                      |

### Utility

| Commands     | Arguments | Description                                 |
| ------------ | --------- | ------------------------------------------- |
| Author       | <none>    | Display project author.                     |
| BotInfo      | <none>    | Display the bot information.                |
| ListCommands | <none>    | List all available commands.                |
| Ping         | <none>    | Check the status of the bot.                |
| Source       | <none>    | Display the source code via a GitLab link.  |
| Uptime       | <none>    | Displays how long the bot has been running. |
| Version      | <none>    | Display the bot version.                    |
| help         | (Word)    | Display a help menu                         |
