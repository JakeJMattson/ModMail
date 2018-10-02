### Warmbot - A helpful report management discord bot

#### What is WarmBot?
Warmbot is as described above. A report management bot. It provides a primary commnunication system between **your server staff**
and **your users**. 

#### How does it work?

Say any given user on your server has a problem. In an ordinary server, they have to ping you or message you, or message a staff member
in order to get the problem solved. This is bad because the staff member might not be online, and they might not know the answer to
the question, whereas another staff member may, so screenshot passing ensues and that just causes duplication, repeating information - 
it's a mess. Introducing Warmbot:


##### The user sees this
![Warmbot](https://i.imgur.com/EJEC0Eu.png)


##### They send it a message and it opens a communication channel with that user like so

![Channel Example](https://i.imgur.com/NUCtmNZ.png)

At this point:
 - Anyone with access to the created text channel can talk with the user through the bot
 - The user can send messages to the bot, and it'll be piped to that channel.


##### What happens when the report is finished? 
You have three options here:
 - Simply delete the channel
 - In the channel, type !!close - this will do the same as delete the channel
 - Type !!archive - This will automatically take a copy of the conversation, and pipe it to the archive channel in the form of a text document. You can go and download this later for reference.

###### Requirements to build
 - Java 9/10
 - Maven on the command line

##### Installation guide
 1. Git pull the repository or download the zip.
 2. After extracting the zip, open up the directory in command prompt
 3. Run the command `mvn clean package`
 4. Go create a directory somewhere that you want the bot to run. This should be outside of the project or on a server. This is your working directory.
 5. In the project directory, open the `target` folder, inside of it, grab the `jar-with-dependencies` - move it to the working directory. 
6. Open the working directory in command prompt. 
7. Run the command `java -jar <jarname>` - replace <jarname> with the file name.
8. This will generate a default config, use the below guide to configure the bot correctly, then re-run this command.

##### How do I configure it

Below, you can find the configuration fields explained, if you want to set up a bot instance this is useful: 

###### Single guild configuration: 

```json
{
	"token": "This should be the bot token",
	"maxOpenReports": 50, -- this is the max number of reports that can be opened in any configured guild.
	"recoverReports": true, -- whether or not the reports will be recovered if the bot goes offline. Saves to disk if true.
	"guildConfigurations": [{
			"guildId": "This is the guild your instance of the bot will run on - it should be the guild ID",
			"reportCategory": "This is the category in which report channels will be made - it should be the report category ID",
			"archiveChannel": "This is where the archived reports will go - it should be the channel ID",
			"prefix": "The command prefix for this guild, e.g. !",
			"staffRoleName": "Anyone with this role will have access to the bot, anyone without won't."
		}
	]
}
```

Warmbot can also be configured for use with multiple guilds below is an example of what that configuration would look like. 
You will have to add an additional guild configuration for each guild you intend to support. 

###### Multi-guild configuration: 

```json
{
	"token": "This should be the bot token",
	"maxOpenReports": 50, -- this is the max number of reports that can be opened in any configured guild.
	"recoverReports": true, -- whether or not the reports will be recovered if the bot goes offline. Saves to disk if true.
	"guildConfigurations": [{
			"guildId": "This is the guild your instance of the bot will run on - it should be the guild ID",
			"reportCategory": "This is the category in which report channels will be made - it should be the report category ID",
			"archiveChannel": "This is where the archived reports will go - it should be the channel ID",
			"prefix": "The command prefix for this guild, e.g. !",
			"staffRoleName": "Anyone with this role will have access to the bot, anyone without won't."
		},
		{
			"guildId": "This is the second guild your instance of the bot will run on - it should be the guild ID",
			"reportCategory": "This is the category in which report channels will be made - it should be the report category ID",
			"archiveChannel": "This is where the archived reports will go - it should be the channel ID",
			"prefix": "The command prefix for this guild, e.g. !",
			"staffRoleName": "Anyone with this role will have access to the bot, anyone without won't."
		}
	]
}
```

