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
Refer to [commands.md](commands.md) for a general list and explanation of all available commands.
To learn about commands during runtime, use the `help` command!