## Requirements to build

## Installation guide

### Server Setup
If you don't already have one, create a Discord server for the bot to run on. 
Follow the [official guide](https://support.discordapp.com/hc/en-us/articles/204849977-How-do-I-create-a-server-) if needed.

### Bot Account
Create a bot account in the [developers](https://discordapp.com/developers/applications/me) section of the Discord website.
- Create an application
- Under "General Information" 
	- Enter an app icon and a name.
	- You will need the client ID for later in this guide; copy it somewhere.
- Under "Bot"
	- Create a bot.
	- Give it a username, app icon, and record the token for future use.
		- Note: This is a secret token, don't reveal it!
	- Uncheck the option to let it be a public bot so that only you can add it to servers.
- Save changes

### Add Bot
- Visit the [permissions](https://discordapi.com/permissions.html) page. Required permissions have been configured for you.
- Under "OAth URL Generator" enter the bot's client ID that you got earlier.
- Click the link to add it to your server.
- It is recommended to place it at the top of your server so members can see it.

## Configuration

Below, you can find an explanation of each configuration field.
WarmBot can be configured for use with multiple guilds.
Add an additional guild configuration for each guild you intend to support. 

```json
{
	"token": "Your bot token",
	"prefix": "The command prefix for this guild, e.g. !",
	"maxOpenReports": "The max number of reports that can be opened in any configured guild",
	"recoverReports": "Whether or not the reports will be recovered if the bot goes offline. Saves to disk if true",
	"guildConfigurations": 
	[
	    {
	        "guildId": "ID of the guild you wish to use the bot in",
	        "reportCategory": "ID of the category in which report channels will be created",
	        "archiveChannel": "ID of channel where archived reports will be sent",
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