## WarmBot - A helpful report management discord bot

### What is WarmBot?
WarmBot is as described above. A report management bot. It provides a primary communication system between **your server staff**
and **your users**. 

### How does it work?

Say any given user on your server has a problem. In an ordinary server, they have to ping you or message you, or message a staff member
in order to get the problem solved. This is bad because the staff member might not be online, and they might not know the answer to
the question, whereas another staff member may, so screenshot passing ensues and that just causes duplication, repeating information - 
it's a mess. Introducing WarmBot:

#### The user sees this
![WarmBot](https://i.imgur.com/EJEC0Eu.png)

#### They send it a message and it opens a communication channel with that user like so
![Channel Example](https://i.imgur.com/NUCtmNZ.png)

At this point:
 - Anyone with access to the created text channel can talk with the user through the bot
 - The user can send messages to the bot, and it'll be piped to that channel.

#### What happens when the report is finished? 
You have three options here:
 - Simply delete the channel
 - In the channel, type !!close - this will do the same as delete the channel
 - Type !!archive - This will automatically take a copy of the conversation, and pipe it to the archive channel in the form of a text document. You can go and download this later for reference.

##### Requirements to build
 - Java 9/10
 - Maven on the command line

#### Installation guide
1. Git pull the repository or download the zip.
2. After extracting the zip, open up the directory in command prompt
3. Run the command `mvn clean package`
4. Go create a directory somewhere that you want the bot to run. This should be outside of the project or on a server. This is your working directory.
5. In the project directory, open the `target` folder, inside of it, grab the `jar-with-dependencies` - move it to the working directory. 
6. Open the working directory in command prompt. 
7. Run the command `java -jar <jarname>` - replace <jarname> with the file name.
8. This will generate a default config, use the below guide to configure the bot correctly, then re-run this command.

#### How do I configure it

Below, you can find an explanation of each configuration field.
WarmBot can be configured for use with multiple guilds.
Add an additional guild configuration for each guild you intend to support. 

##### Configuration: 

```json
{
	"token": "Your bot token",
	"maxOpenReports": "The max number of reports that can be opened in any configured guild",
	"recoverReports": "Whether or not the reports will be recovered if the bot goes offline. Saves to disk if true",
	"guildConfigurations": 
	[
	    {
	        "guildId": "ID of the guild you wish to use the bot in",
	        "reportCategory": "ID of the category in which report channels will be created",
	        "archiveChannel": "ID of channel where archived reports will be sent",
	        "prefix": "The command prefix for this guild, e.g. !",
	        "staffRoleName": "Role required to use the bot"
	        "loggingConfiguration": 
	        {
                    "loggingChannel": "ID of channel where messages will be logged",
                    "logStartup": "log when the bot initializes",
                    "logMemberOpen": "log when a member open a report",
                    "logStaffOpen": "log when a staff member open a report",
                    "logArchive": "log when a report is archived",
                    "logClose": "log when a report is closed"
	        }
	    },
	    {
	        <Additional guilds>
	    }
	]
}
```

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
| Close     | (none)`*` | Close report.                             |
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