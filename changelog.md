# Version 2.1.0

### New Features
```
* Auto setup       - Automatically create required channels. If some of these channels exist, smart bind by name.
* Permissions sync - The move command will now sync permissions with the new category. (Option to prevent sync)
* Log commands     - Remove more in-place embeds and replace them with plain text logs.
* Add JUnit tests  - Added the backbone for adding tests and implemented test for configuration commands.
```

### New Commands
```
* SetPresence - Set the Discord presence of the bot.
```

### Fixes
```
* Ignore (do not log) audit log events from self.
* Remove all instances of hard-coded prefixes - config only.
* Fix issue where the invokation of the archive command was being archived.
```

# Version 2.0.1

### New Commands
```
* SetLoggingChannel - Set the target logging channel during runtime.
* Info              - Access report data such as user ID's.
* Move              - Move a target report to a different category.
* Tag               - Prepend a tag to the name of this report channel.
* ResetTags         - Reset a report channel to its original name.
```

### Misc Changes
```
* Added Docker deployment script and instructions for Windows.
* Report open embeds and edit embeds now contain a user's avatar.
* Message edits are now logged in the logging channel instead of in-place.
* Added an argument to the archive command to allow leaving notes next to files.
```

### Bug Fixes
```
* Logging service - Command events used in reports where users were no longer in the server would not log due to JDA.
* Logging service - Reports closed by channel deletion would not log due to human oversight.
* Archiving       - Messages containing links were transcribed as empty embeds due to Discord's preview feature.
* Editing         - Edits were intentionally not being sanitized due to lack of ping risk. They are now sanitized.
```

# Version 2.0.0

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

# Version 1.5.1 and earlier

### Features
```
* Basic report functionality
```

### Commands
```
* Author  - Display the author of the bot.
* Source  - Display the GitLab repository link.
* Ping    - Display the network status of the bot.
* Help    - Display a basic static help menu.
* Close   - Close the report channel this command was invoked in.
* Archive - Archive the report channel this command was invoked in. This transcribes the report to a text document.
```
