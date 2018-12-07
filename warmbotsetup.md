## Requirements to build

## Installation guide

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