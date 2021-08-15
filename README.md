# Frostwall Bot

This is an anti scam link bot designed to monitor and prevent posting of malicious links in a discord server.


## Requirements
- Orace JDK 15 or OpenJDK 15 minimum


[Latest openjdk](https://jdk.java.net/)

[Latest Oracle JDK](https://www.oracle.com/java/technologies/javase-downloads.html)

Note that thanks to backwards compatability, the latest jdk will run older versions just fine. 

For linux installation of openjdk [read here](https://openjdk.java.net/install/) for more info... [or here](https://www.linuxuprising.com/2020/09/how-to-install-oracle-java-15-on-ubuntu.html)... and [here](https://aboullaite.me/switching-between-java-versions-on-ubuntu-linux/) for info on how to manage multiple installed versions. Just look up how for your specific distro.
## Usage

```bash
java -jar frostwall.jar
```
### Discord usage

```
!f check <https://example.com/> - runs a check on the link passed in and returns the results. Allows to add and manage stored domains.
```


More information in the discordbot.yaml files.


## Goals
- This bot is designed to mitigate phishing and scam links posted in discord by allowing for an easily managed database of malicious and clean domains.
- The longer the bot is ran and approvals are managed, the better the bot will be at stopping unwanted links.

## Features
- Scans every message sent (channels configured in config) for links and runs an analysis using VirusTotal.com api.
  Based on the results, a link will either be marked as malicious, suspicious, or clean. An approval embed is then posted to allow moderators to either allow or deny the domain
  while handling the message sent as well.
  Analysis example: [https://www.virustotal.com/gui/domain/discord.com](https://www.virustotal.com/gui/domain/discord.com)
- Reaction operatable embeds to allow for approval of detected links. 
- All analyzed links are stored in a database to query against for future links. This improves performance rather than scanning all links everytime.
- Links are stored with a parameter isAllowed which lets the bot know wether to ignore a link or to handle a disallowed link.
