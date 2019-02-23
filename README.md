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
This is for developers: Refer to [warmbotsetup.md](warmbotsetup.md) for full setup instructions.
Users should scroll down to [here instead](#docker)

## Commands
Below you can find a list and explanation of all available commands.

### Owner

`These commands can only be run by the owner of the bot.`

| Command       | Arguments | Effect                                |
| ------        | ------    | ------                                |
| Whitelist     | Guild ID  | Add a guild to the whitelist.         |
| UnWhitelist   | Guild ID  | Remove a guild from the whitelist.    |
| ShowWhitelist | (none)    | Display all guilds in the whitelist.  |

### Configuration

`These commands can only be run by the owner of the guild.`

| Command           | Arguments     | Effect                                            |
| ------            | ------        | ------                                            |
| SetReportCategory | Category ID   | Set the category where new reports will be opened.|
| SetArchiveChannel | Channel ID    | Set the channel where reports will be archived.   |
| SetStaffRole      | Role name     | Set the role required to use this bot.            |

### Report

`These commands can only be run in a report channel.`

| Command   | Arguments | Effect                                    |
| ------    | ------    | ------                                    |
| Close     | (none)    | Close report and notify user.             |
| Archive   | (none)    | Transcribe report to text (closes report).|
| Note      | (none)    | Produce a note in the form of an embed.   |

### Report Helpers

| Command   | Arguments | Effect                                    |
| ------    | ------    | ------                                    |
| Open      | User ID   | Open a report with the target user.       |
| CloseAll  | (none)    | Close all reports in the current guild.   |

### Utility

| Command   | Arguments | Effect                                    |
| ------    | ------    | ------                                    |
| Author    | (none)    | Display the author of the bot.            |
| Ping      | (none)    | Display the status of the bot.            |
| Source    | (none)    | Display the source code via a GitLab link.|
| Version   | (none)    | Display the current running version.      |
| BotInfo   | (none)    | Display a summary or bot information.     |
| Uptime    | (none)    | Display the amount of time online.        |


## How to deploy a Warmbot instance

#### tl;dr for those of you who know docker
1. Clone and cd into the root `cd /Warmbot`
2. `./scripts/deploy.sh <YOUR_BOT_TOKEN> <ABSOLUTE_PATH_TO_CONFIG_FOLDER>`

#### Lengthy explanation for those of you new to docker

If you want to run a warmbot instance yourself, thankfully this is very easy. The steps are listed 
below
1. Firstly download and install docker.
2. Clone this repository: `git clone https://gitlab.com/Aberrantfox/WarmBot.git` - you can also just 
 download and extract the zip file.
3. Open a terminal or command prompt
4. `cd /Warmbot` - cd into the directory
5. `./scripts/deploy.sh <YOUR_BOT_TOKEN> <CONFIG_PATH>` 
    - replace <YOUR_BOT_TOKEN> with a valid discord bot token.
    - replace <CONFIG_PATH> with a path to where you want the bot configuration to be.
      It's recommended to just make a folder called `/home/me/config`.
6. Example run `./scripts/deploy.sh aokspdf.okwepofk.34p1o32kpo,pqo.sASDAwd /home/me/config`
   *note: The token is fake :)* 
 