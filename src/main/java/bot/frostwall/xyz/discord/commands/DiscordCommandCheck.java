package bot.frostwall.xyz.discord.commands;

import java.net.URI;
import java.net.URISyntaxException;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import bot.frostwall.xyz.discord.DiscordBot;
import bot.frostwall.xyz.discord.DiscordUtils;
import bot.frostwall.xyz.logger.Logger;
import bot.frostwall.xyz.logger.Logger.Level;

public class DiscordCommandCheck extends Command {

	private String feature = "discord_command_check";
	public DiscordCommandCheck() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		if (event.getArgs().isBlank()) {
			return;
		}
		
		if (!CommandUtils.fullUsageCheck(event, feature)) {
			return;
		}
		
		String domain = null;
		URI uri = null;
		
		try {
			uri = new URI(event.getArgs());
			domain = uri.getHost();
			
		} catch (URISyntaxException e) {
			Logger.log(Level.ERROR, "URISyntaxException occurred: " + e.toString());
		}
		
		if (domain == null) {
			event.reply("Invalid url provided. Example: `https://discord.com/`");
			return;
		}
		
		
		DiscordUtils.sendCheckMessage(event, domain, event.getArgs());
	}

}
