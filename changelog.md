# Changelog

## Version 2.0

### Features

```
* Multi-guild       - A single instance of this bot can now be used across multiple guilds.
* Event propagation - User and staff events can now be forwarded through the bot. 
                      User typing events and message edits will be forwarded to the private channel. 
                      Staff edits and deletes will be forwarded to the user.
* Report recovery   - Reports can now be saved to disk and reloaded if the bot goes offline instead of losing reports.
* Leave listener    - Create an embed in a report channel if the user that owns this report leaves or is banned.
* Logging           - Log events such as channel creation / deletiion (and other events) into a logging channel.
* Whitelisting      - Ignore commands in non-staff channels.
```

### Commands

```
* CloseAll - Close all of the currently open reports on the server. This does not have to be invoked in a report channel.
* Open     - Open a report with the target user. 
* Version  - Display the version of this instance of the bot.
* BotInfo  - Display various bot information (author; contributors; source; version).
* Uptime   - Display time the bot has been online since startup.
```