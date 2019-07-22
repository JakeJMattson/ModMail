## Discord Guide

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
- Visit the [permissions](https://discordapi.com/permissions.html) page.
- Under "OAth URL Generator" enter the bot's client ID that you got earlier.
- Click the link to add it to your server.
- It is recommended to place it at the top of your server so members can see it.

## Build Guide

### Build Requirements

* [IntelliJ](https://www.jetbrains.com/idea/download/#section=windows) or another Maven compatible IDE.
* [Java 11](https://jdk.java.net/11/) or your preferred version.

### Building

Import the project as a Maven project. Maven will handle all dependencies for you and prepare the project.
When running the project for the first time, the config file will be created for you.
Please fill it out and run the bot again. If you don't understand the fields, consult the section below.
Once your configuration is complete, you can run the bot again.
If everything was set up properly, your bot should now be up and running!

## Deploy Guide

### Windows

#### Abridged version

1. Clone and cd into the root `cd /WarmBot`
2. `%CD%/scripts/deploy.bat <YOUR_BOT_TOKEN> <ABSOLUTE_PATH_TO_CONFIG_FOLDER>`

#### Full version

1. Download and install the docker toolbox.
2. Clone this repository: `git clone https://gitlab.com/Aberrantfox/WarmBot.git` - 
    you can also just download and extract the zip file.
3. Open the command prompt
4. `cd /WarmBot` - cd into the directory
5. `%CD%/scripts/deploy.bat <YOUR_BOT_TOKEN> <CONFIG_PATH>` 
    - replace <YOUR_BOT_TOKEN> with a valid discord bot token.
    - replace <CONFIG_PATH> with a path to where you want the bot configuration to be.
    
    **Important:** The paths required for a correct deployment on Windows are very specific.
    In order to mount correctly, the folder on your local machine must be within the shared folders of the VM.
    By default, the shared folder list is exclusively `C:\Users`. This includes all subdirectories. 
    It also requires a very specific format - using forward slashes, instead of the traditional Windows format.
    It's recommended to make a folder with a similar path to this: `/c/Users/account/warmbot` to store configurations.
    
6. Example run `%CD%/scripts/deploy.bat aokspdf.okwepofk.34p1o32kpo,pqo.sASDAwd /c/Users/account/warmbot`
   *note: The token is fake :)* 

## Linux

#### Abridged version

1. Clone and cd into the root `cd /WarmBot`
2. `./scripts/deploy.sh <YOUR_BOT_TOKEN> <ABSOLUTE_PATH_TO_CONFIG_FOLDER>`

#### Full version

1. Download and install docker.
2. Clone this repository: `git clone https://gitlab.com/Aberrantfox/WarmBot.git` -
    you can also just download and extract the zip file.
3. Open a terminal or command prompt
4. `cd /WarmBot` - cd into the directory
5. `./scripts/deploy.sh <YOUR_BOT_TOKEN> <CONFIG_PATH>` 
    - replace <YOUR_BOT_TOKEN> with a valid discord bot token.
    - replace <CONFIG_PATH> with a path to where you want the bot configuration to be.
      It's recommended to just make a folder called `/home/me/config`.
6. Example run `./scripts/deploy.sh aokspdf.okwepofk.34p1o32kpo,pqo.sASDAwd /home/me/config`
   *note: The token is fake :)* 

### Configuration

Below, you can find an explanation of each configuration field.

```json
{
	"ownerId": "The ID of the bot owner - has access to the whitelist commands",
	"prefix": "The command prefix for this guild, e.g. !",
	"maxOpenReports": "The max number of reports that can be opened in any configured guild",
	"whitelist": [
	    "ID of a guild allowed to use this bot.", 
	    "<Additional guilds>"
        ],
	"guildConfigurations": [
	    {
	        "guildId": "ID of the guild you wish to use the bot in",
	        "reportCategory": "ID of the category in which report channels will be created",
	        "archiveChannel": "ID of channel where archived reports will be sent",
	        "staffRoleName": "Name of the role required to use the bot",
	        "staffChannels": [
                "The channels in which this bot can respond to commands. Includes the report category automatically."
                ],
	        "loggingConfiguration": 
	        {
                    "loggingChannel": "ID of channel where messages will be logged",
                    "logEdits": "log user edits made in a report",
                    "logCommands": "log staff command execution",
                    "logStartup": "log when the bot initializes",
                    "logMemberOpen": "log when a member open a report",
                    "logStaffOpen": "log when a staff member open a report",
                    "logArchive": "log when a report is archived",
                    "logClose": "log when a report is closed"
	        }
	    }
	]
}
```