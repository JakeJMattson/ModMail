# WarmBot - A report management Discord bot

WarmBot is a report management bot designed to provide a communication system between server staff and other members.
In an ordinary server, users typically need to ping or message a staff member in order to get an issue resolved.
This can create complications, such as the staff member not being online, not knowing the answer, being too busy to respond, or countless other issues.
Other staff may be a perfect fit for the issue, but the member has no way of knowing who to message or ping.
This can lead to passing around screenshots of conversations, information duplication, and in short, a big mess.
WarmBot fixes this.
<br>
![WarmBot](https://i.imgur.com/EJEC0Eu.png)

## Reports
Reports are private text channels that allow the entire staff team to communicate with a single member.
You can have as many of these as you want (limited by configuration and Discord).
<br>
![Reports](https://i.imgur.com/7vgwc9E.png)
<br>

## Member experience
### Opening a report
To open a report as a member, simply message the bot.
A new report will be opened automatically.

![Member](https://i.imgur.com/tf9k6PI.png)

### Using a report
Once a report is opened, you can respond to the bot as if it were any other conversation.
All messages sent to the bot will be forwarded to your report channel for staff to see.

## Staff experience
### Opening a report
If a report needs to be opened from the staff side, you can use the `open` command.
This will attempt to create a new report with the target user.

### Using a report
Once a report is opened, anyone with access to this private channel can talk with the user through the bot.
Talking through WarmBot has the added benefit of masking staff identities.
If things become hostile, the user will have no idea who they're talking to.

![Staff](https://i.imgur.com/tRLMPwj.png)

#### Closing a report
##### From Discord
 * Delete the channel - WarmBot will detect the event and close the report for you.

##### Using commands
 * In the report channel, `close` - This has the same effect as deleting the channel.
 * In the report channel, `archive` - Transcribes the report to text, archives it, then closes the report.

##### Automatically
Reports can be configured to automatically close a set time after the most recent staff response.
This feature will close "dead reports" where the issue has been resolved but the report was not closed.

## Setup
Refer to [warmbotsetup.md](warmbotsetup.md) for full setup instructions.

## Commands
Below you can find a list and explanation of all available commands.

### Configuration

`Note: These commands can only be run by the owner of the guild.`

| Command           | Arguments     | Effect                                            |
| ------            | ------        | ------                                            |
| Setup             | (none)        | Initiate the setup conversation.                  |
| SetReportCategory | Category ID   | Set the category where new reports will be opened.|
| SetArchiveChannel | Channel ID    | Set the channel where reports will be archived.   |
| SetStaffRole      | Role name     | Set the role required to use this bot.            |

### Report

| Command   | Arguments | Effect                                    |
| ------    | ------    | ------                                    |
| Open      | User ID   | Open a report with the target user.       |
| Close     | (none)`*` | Close report and notify user.             |
| CloseAll  | (none)    | Close all reports in the current guild.   |
| Archive   | (none)`*` | Transcribe report to text (closes report).|

`*The invocation channel must be a report channel.`

### Utility

| Command   | Arguments | Effect                                    |
| ------    | ------    | ------                                    |
| Author    | (none)    | Display the author of the bot.            |
| Ping      | (none)    | Display the status of the bot.            |
| Source    | (none)    | Display the source code via a GitLab link.|
| Version   | (none)    | Display the current running version.      |
| BotInfo   | (none)    | Display a summary or bot information.     |
| Uptime    | (none)    | Display the amount of time online.        |