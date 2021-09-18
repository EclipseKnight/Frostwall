package bot.frostwall.xyz.discord.commands;

import java.util.ArrayList;
import java.util.List;

import com.jagrosh.jdautilities.command.CommandEvent;

import bot.frostwall.xyz.discord.DiscordBot;
import bot.frostwall.xyz.discord.DiscordUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;

public class CommandUtils {

	/**
	 * Performs a full permission check.
	 * @param event
	 * @param feature
	 * @return
	 */
	public static boolean fullUsageCheck(CommandEvent event, String feature) {
		boolean result = true;
		String reply = "";
		
		//bypass if the user is an owner or co-owner.
		if (isOwner(event)) {
			return true;
		}
		
		if (!isFeatureEnabled(feature)) {
			reply += "Command is disabled. ";
			result = false;
		}
		
		if (!correctChannel(event, feature)) {
			reply += "Command is disabled in this channel. ";
			result = false;
		}
		
		if (!canUseCommand(event, feature)) {
			reply += "You do not have the required role. ";
			result = false;
		}
		
		if (!reply.isEmpty()) {
			EmbedBuilder eb = new EmbedBuilder();
			eb.setAuthor(event.getAuthor().getAsTag(), null, event.getAuthor().getAvatarUrl());
			eb.appendDescription(reply);
			eb.setFooter("Reasons for error are listed.");
			eb.setColor(DiscordBot.COLOR_FAILURE);
			DiscordUtils.sendTimedMessage(event, eb.build(), 15000, false);
		}
		
		return result;
	}
	
	//is user owner or co-owner.
	public static boolean isOwner(CommandEvent event) {
		String userId = event.getMember().getId();
		if (userId.equals(DiscordBot.configuration.getOwnerId())) {
			return true;
		}
		
		for (String id: DiscordBot.configuration.getCoOwnerIds()) {
			if (userId.equals(id)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the feature is enabled.
	 * @param feature
	 * @return
	 */
	public static boolean isFeatureEnabled(String feature) {
		return DiscordBot.configuration.getFeatures().get(feature).isEnabled();
	}
	
	
	/**
	 * Checks if command is allowed in used channel.
	 * @param event
	 * @param feature
	 * @return
	 */
	public static boolean correctChannel(CommandEvent event, String feature) {
		List<String> channels = DiscordBot.configuration.getFeatures().get(feature).getChannels();
		
		if (channels == null || channels.get(0) == null) {
			return true;
		}
		
		if (channels.contains(event.getChannel().getId())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if the user can use the command based on roles.
	 * @param event
	 * @param feature
	 * @return
	 */
	public static boolean canUseCommand(CommandEvent event, String feature) {
		
		List<String> cmdRoles = DiscordBot.configuration.getFeatures().get(feature).getRoles();
		
		if (cmdRoles == null || cmdRoles.get(0) == null) {
			return true;
		}
		
		List<String> sRoles = new ArrayList<>();
		
		for (Role r : event.getMember().getRoles()) {
			sRoles.add(r.getId());
		}
		
		for (String pRole: sRoles) {
			for (String cmdRole: cmdRoles) {
				if (pRole.equals(cmdRole)) {
					return true;
				}
			}
		}
		
		return false;
	}
}
