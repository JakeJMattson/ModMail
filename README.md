## WarmBot - A report management Discord bot

### What is WarmBot?
WarmBot is a report management bot designed to provide a communication system between server staff and other members.
In an ordinary server, users typically need to ping or message a staff member in order to get an issue resolved.
This creates even more complications; the target staff member may not: be online, know the answer, care to respond, or countless other issues.
Other staff may be a perfect fit for the issue, but the member has no way of knowing who to message.
This can lead to passing around screenshots of conversations, information duplication, and in short, a big mess.
WarmBot fixes this.

### How does it work?
First the bot is paced at the top of the server where the users can see it.
It acts as a beacon for questions.
<br>

![WarmBot](https://i.imgur.com/EJEC0Eu.png)

Next, the user sends it a message. 
This opens a communication channel between the user and every staff on the server in the form of a private channel.

![Channel Example](https://i.imgur.com/NUCtmNZ.png)

Any messages the users sends the bot are automatically sent to that channel.
Anyone with access to this private channel can talk with the user through the bot.
Talking through WarmBot has the added benefit of masking staff identities.
If things become hostile, the user will have no idea who they're talking to.

#### Closing a report

##### From Discord
 * Delete the private channel - WarmBot will detect the event and close the report for you.
 

##### Using commands
 * In the report channel, `close` - This has the same effect as deleting the channel.
 * In the report channel, `archive` - Transcribes the report to text, archives it, then closes the report.
 * In any staff channels, `closeall` - Closes all open reports in the server.

### Setup
Refer to [warmbotsetup.md](warmbotsetup.md) for full setup instructions.

### Commands: 

#### Configuration

`Note: These commands can only be run by the owner of the guild.`

| Command           | Arguments     | Effect                                            |
| ------            | ------        | ------                                            |
| Setup             | (none)        | Initiate the setup conversation.                  |
| SetReportCategory | Category ID   | Set the category where new reports will be opened.|
| SetArchiveChannel | Channel ID    | Set the channel where reports will be archived.   |
| SetStaffRole      | Role name     | Set the role required to use this bot.            |

#### Report

| Command   | Arguments | Effect                                    |
| ------    | ------    | ------                                    |
| Open      | User ID   | Open a report with the target user.       |
| Close     | (none)`*` | Close report and notify user.             |
| CloseAll  | (none)    | Close all reports in the current guild.   |
| Archive   | (none)`*` | Transcribe report to text (closes report).|

`*The invocation channel must be a report channel.`

#### Utility

| Command   | Arguments | Effect                                    |
| ------    | ------    | ------                                    |
| Author    | (none)    | Display the author of the bot.            |
| Ping      | (none)    | Display the status of the bot.            |
| Source    | (none)    | Display the source code via a GitLab link.|
| Version   | (none)    | Display the current running version.      |
| BotInfo   | (none)    | Display a summary or bot information.     |
| Uptime    | (none)    | Display the amount of time online.        |