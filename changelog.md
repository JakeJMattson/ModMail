# **Version 2.0**

### New Features

```
* Multi-guild       - A single instance of this bot can now be used across multiple guilds.
* Event propagation - User and staff events can now be forwarded through the bot. 
                      User typing events and message edits will be forwarded to the private channel. 
                      Staff edits and deletes will be forwarded to the user.
* Report recovery   - Reports can now be saved to disk and reloaded if the bot goes offline instead of losing reports.
* Leave listener    - Create an embed in a report channel if the user that owns this report leaves or is banned.
* Logging           - Log events such as channel creation / deletion (and other events) into a logging channel.
* Whitelisting      - Ignore commands in non-staff channels. Leave non-whitelisted servers or initialize setup.
```

### New Commands

```
* Whitelist     - Add a guild to the whitelist.
* UnWhitelist   - Remove a guild from the whitelist.
* ShowWhitelist - Display all guilds in the whitelist.
```
```
* SetStaffRole      - Set the role required to use this bot.
* SetReportCategory - Set the category where new reports will be opened.
* SetArchiveChannel - Set the channel where reports will be archived.
```
```
* CloseAll - Close all of the currently open reports on the server.
* Open     - Open a report with the target user.
* Note     - Send an embed in the invoked channel as a note.
* Version  - Display the version of this instance of the bot.
* BotInfo  - Display various bot information (author; contributors; source; version).
* Uptime   - Display time the bot has been online since startup.
```

### Misc Changes

```
* The help system was upgraded through KUtils to be interactive.
* New reports start with embeds instead of plain text.
* The archive command can now handle embeds.
```

# **Version 1.5.1 and earlier**

### Features

```
Basic functionality
```

### Commands

```
Author  - Display the author of the bot.
Source  - Display the GitLab repository link.
Ping    - Display the network status of the bot.
Help    - Display a basic static help menu.
Close   - Close the report channel this command was invoked in.
Archive - Archive the report channel this command was invoked in. This transcribes the report to a text document.
```
