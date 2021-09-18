package bot.frostwall.xyz;

import java.io.File;

import org.fusesource.jansi.AnsiConsole;

import bot.frostwall.xyz.database.JsonDB;
import bot.frostwall.xyz.discord.DiscordBot;
import bot.frostwall.xyz.logger.Logger;
import bot.frostwall.xyz.logger.Logger.Level;

public class Launcher {

	/**
	 * User working directory.
	 */
	public static String uwd = System.getProperty("user.dir");
	
	public static String botDir = System.getProperty("user.dir") + File.separator + "frostwall" + File.separator;
	
	
	/**
	 * Discord bot instance.
	 */
	public static DiscordBot discordBot;
	
	
	public static void main(String[] args) {
		//arg[0] folder to generate and store everything.
		if (args.length > 0) {
			uwd = args[0];
			botDir = uwd + File.separator + "frostwall" + File.separator;
			
		}  
		
		// allows ANSI escape sequences to format console output. For loggers. aka PRETTY COLORS
		AnsiConsole.systemInstall();
		
		Logger.log(Level.INFO, "Inializing Database...");
		JsonDB.init();
		
		Logger.log(Level.INFO, "Starting Discord Bot...");
		discordBot = new DiscordBot();
		
		// initialize scheduled backups after discord bot creation. 
		// This utilizes the discord bot configuration file instead of a separate config.
		JsonDB.initScheduledBackups();
		
	}
	
}
