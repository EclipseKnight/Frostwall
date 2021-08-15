package bot.frostwall.xyz.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import bot.frostwall.xyz.discord.DiscordBot;

public class DiscordCommandTest extends Command {

	private String feature = "discord_command_test";
	public DiscordCommandTest() {
		this.name = DiscordBot.configuration.getFeatures().get(feature).getName();
		this.aliases = DiscordBot.configuration.getFeatures().get(feature).getAliases();
	}
	
	@Override
	protected void execute(CommandEvent event) {
		event.reply("Nothing is broken!");
		
	}

}
