package bot.frostwall.xyz.discord;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.security.auth.login.LoginException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jagrosh.jdautilities.command.CommandClientBuilder;

import bot.frostwall.xyz.Launcher;
import bot.frostwall.xyz.discord.commands.DiscordCommandCheck;
import bot.frostwall.xyz.discord.commands.DiscordCommandTest;
import bot.frostwall.xyz.discord.events.DiscordEventMessageAnalyzer;
import bot.frostwall.xyz.logger.Logger;
import bot.frostwall.xyz.logger.Logger.Level;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DiscordBot {

	public static final String VERSION = "v1.0.1";
	public static final String PREFIX = "!f ";
	
	public static final int COLOR_SUCCESS = 65395;
	public static final int COLOR_FAILURE = 16711748;
	public static final int COLOR_WARN = 16229903;
	public static final int COLOR_FROST = 175323;
	
	public static final String RED_TICK = "U+274C";
	public static final String GREEN_TICK = "U+2714";
	
	public static DiscordConfiguration configuration;
	
	/**
	 * JDA API
	 */
	public static JDA jda = null;
	
	/**
	 * Command builder jdautilities
	 */
	private CommandClientBuilder builder;
	
	
	public DiscordBot() {
		// Load configuration
		loadConfiguration();
		
		
		if (configuration.getApi().get("discord_client_id") == null 
        		|| configuration.getApi().get("discord_client_token") == null
        		|| configuration.getOwnerId() == null) {
			
        	Logger.log(Level.FATAL, "Discord id or token or owner id is not set. Check the discordbot.yaml keys: discord_client_id, discord_client_token, and owner_id values.");
        	Logger.log(Level.FATAL, "Exiting...");
        	System.exit(1);
        } 
		
		try {
			jda = JDABuilder.create
					(
							configuration.getApi().get("discord_client_token"), 
							
							GatewayIntent.DIRECT_MESSAGES,
							GatewayIntent.DIRECT_MESSAGE_REACTIONS,
							
							GatewayIntent.GUILD_MESSAGES,
							GatewayIntent.GUILD_MESSAGE_REACTIONS,
							GatewayIntent.GUILD_BANS,
							GatewayIntent.GUILD_EMOJIS
							
					)
					.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)
					.build()
					.awaitReady();
		} catch (LoginException | InterruptedException e) {
			Logger.log(Level.FATAL, "Discord bot failed to initialize: " + e.toString());
			return;
		}
		
		// create a command builder to register commands.
		builder = new CommandClientBuilder();
		
		// Sets the owner id (commands can be set to owner/co-owner only)
		builder.setOwnerId(configuration.getOwnerId());
		
		// Sets the co owners. #setCoOwnerIds takes a variable argument so converting from List to string[] is necessary.
		builder.setCoOwnerIds(configuration.getCoOwnerIds().toArray(new String[configuration.getCoOwnerIds().size()]));
		
		// Sets the command prefix 
		builder.setPrefix(PREFIX);
		
		// Sets the default help command
		builder.setHelpWord("help");
		
		
		registerCommands(builder);
		
		
		// Sets the status and name of the bot.
		DiscordUtils.setBotStatus("for scam links...");
		jda.getSelfUser().getManager().setName(configuration.getBot().get("name"));
	}

	private void registerCommands(CommandClientBuilder builder) {
	
		builder.addCommands(new DiscordCommandTest(),
				new DiscordCommandCheck());
		
		jda.addEventListener(new DiscordEventMessageAnalyzer());
		
		// adds command client to listener. Commands use events will now be checked for on the discord server.
		jda.addEventListener(builder.build());
	}
	
	

	/**
	 * Load the configuration file for the discord bot.
	 */
	public static void loadConfiguration() {
		File discordBotConfig = new File(Launcher.botDir + "configs" + File.separator + "discordbot.yaml");
		
		if (new File(Launcher.botDir + "configs").mkdirs()) {
			Logger.log(Level.WARN, "Generating configs directory...");
		}
		
		try {
    		if (!discordBotConfig.exists()) {
        		generateConfig();
        	}
        	
            InputStream is = new BufferedInputStream(new FileInputStream(discordBotConfig));
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            configuration = mapper.readValue(is, DiscordConfiguration.class);
            
            is.close();
        } catch (Exception e) {
            Logger.log(Level.FATAL, e.toString());
            Logger.log(Level.FATAL, "Unable to load configuration... Exiting.");
            System.exit(1);
        }
		
	}
	
	 /**
     * Generates config file.
     */
    private static void generateConfig() {   	
    	
        try {
        	Logger.log(Level.WARN, "Missing discordbot.yaml. Generating new config...");
        	ClassLoader classloader = DiscordBot.class.getClassLoader();
        	
        	// copies twitchbot.yaml template to current working directory.
        	InputStream original = classloader.getResourceAsStream("discordbot.yaml");
            Path copy = Paths.get(new File(Launcher.botDir + "configs" + File.separator + "discordbot.yaml").toURI());
          
            Logger.log(Level.WARN, "Generating config at " + copy);
            Files.copy(original, copy);
            original.close();
		} catch (IOException e) {
			Logger.log(Level.ERROR, e.toString());
			Logger.log(Level.ERROR, "Failed to generate discordbot.yaml...");
		}
    }
}
