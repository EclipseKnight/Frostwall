package bot.frostwall.xyz.discord.commands.events;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bot.frostwall.xyz.database.JsonDB;
import bot.frostwall.xyz.database.JsonDBUtils;
import bot.frostwall.xyz.database.documents.Approval;
import bot.frostwall.xyz.database.documents.VTDomain;
import bot.frostwall.xyz.discord.DiscordBot;
import bot.frostwall.xyz.discord.DiscordUtils;
import bot.frostwall.xyz.logger.Logger;
import bot.frostwall.xyz.logger.Logger.Level;
import bot.frostwall.xyz.virustotal.VTUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordEventMessageAnalyzer extends ListenerAdapter {

	private String feature = "discord_event_message_analyzer";
	
	//Precompiled pattern and matcher.
	private Pattern pattern = Pattern.compile("((https?):((//)|(\\\\\\\\))+([\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&](#!)?)*)");
	private Matcher matcher = pattern.matcher("");
	
	
	
	/*
	 * Steps:
	 * Check if message has links
	 * Check if the link exists in the database
	 * If it does, check if it is allowed.
	 * If it doesn't, run an analysis 
	 */
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!DiscordBot.configuration.getFeatures().get(feature).isEnabled()) {
			return;
		}
		
		MessageChannel channel = event.getChannel();
		
		// Grab the channels from the config
		List<String> channels = DiscordBot.configuration.getFeatures().get(feature).getChannels();
		
		//To avoid a recursive nightmare of link checking. 
		if (channel.getId().equals(DiscordBot.configuration.getLinkApprovalChannel())) {
			return;
		}
		
		if (!(channels == null || channels.get(0) == null)) {
			// If the channel is blacklisted from analyzing. 
			if (channels.contains(channel.getId())) {
				return;
			}
		}
		
		Message message = event.getMessage();
		
		matcher.reset(message.getContentRaw());
		
		//if matches, then go through them.
		while (matcher.find()) {
			URI uri = null;
			try {
				uri = new URI(matcher.group());
			} catch (URISyntaxException e) {
				Logger.log(Level.ERROR, "Invalid link.");
				continue;
			}
			
			//check if domain exists in database
			VTDomain vtdomain = JsonDBUtils.getVTDomainFromDomain(uri.getHost());
			
			//If it doesn't exist then analyze it
			if (vtdomain == null) {
				analyze(event, uri.getHost(), vtdomain);
				continue;
			}
			
			//if it does then check if its allowed.
			if (!vtdomain.isAllowed()) {
				DiscordUtils.sendDeniedMessage(event, vtdomain, matcher.group());
				event.getMessage().delete().queue();
				event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Detected posting a malicious link! Link has been deleted.").queue();
				
				vtdomain.setCount(vtdomain.getCount() + 1);
				JsonDB.database.upsert(vtdomain);
			} else {
				vtdomain.setCount(vtdomain.getCount() + 1);
				JsonDB.database.upsert(vtdomain);
				return;
			}
		}
		
	}
	
	private void analyze(MessageReceivedEvent event, String domain, VTDomain vtdomain) {
		Map<String, String> attrMap = VTUtils.domainAttributeRequest(domain);
		if (Boolean.valueOf(attrMap.get("error"))) {
			Logger.log(Level.ERROR, "Error occurred upon analyzing link. ");
			return;
		}
		
		vtdomain = new VTDomain();
		vtdomain.setDomain(domain);
		vtdomain.setAttributes(attrMap);
		vtdomain.setCount(vtdomain.getCount() + 1);
		JsonDB.database.upsert(vtdomain);
		
		if (Integer.valueOf(attrMap.get("malicious_stats")) <= 0) {
			DiscordUtils.sendCleanMessage(event, vtdomain, matcher.group());
			return;
		}
		
		if (Integer.valueOf(attrMap.get("malicious_stats")) > 0) {
			DiscordUtils.sendMaliciousMessage(event, vtdomain, matcher.group());
			event.getMessage().delete().queue();
			return;
		}
		
		if (Integer.valueOf(attrMap.get("suspicious_stats")) > 0) {
			DiscordUtils.sendSuspiciousMessage(event, vtdomain, matcher.group());
			event.getMessage().delete().queue();
			return;
		}
	}
	
	
	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getMember().getId().equals(DiscordBot.jda.getSelfUser().getId())) {
			return;
		}
		
		if (!event.getChannel().getId().equals(DiscordBot.configuration.getLinkApprovalChannel())) {
			return;
		}
		
		//Check if message is an approval by checking if it exists in the database.
		Approval approval = JsonDBUtils.getApprovalFromMessageId(event.getMessageId());
		
		//If not then return.
		if (approval == null) {
			return;
		}
		
		if (event.getReactionEmote().isEmoji()
				&& event.getReactionEmote().getAsCodepoints().equalsIgnoreCase(DiscordBot.RED_TICK)) {
			
			handleDeny(event.retrieveMessage().complete(), approval);
		}
		
		if (event.getReactionEmote().isEmoji()
				&& event.getReactionEmote().getAsCodepoints().equalsIgnoreCase(DiscordBot.GREEN_TICK)) {
			
			handleAllow(event.retrieveMessage().complete(), approval);
		}
	}
	
	
	private void handleDeny(Message message, Approval approval) {
		if (message == null) {
			JsonDB.database.remove(approval, Approval.class);
			return;
		}
		
		message.clearReactions().queue();
		
		EmbedBuilder eb = new EmbedBuilder(message.getEmbeds().get(0));
		switch (approval.getType()) {
			case "malicious" -> eb.setTitle("New Malicious Link Detected! (DENIED)");
			case "suspicious" ->  eb.setTitle("New Suspicious Link Detected! (DENIED)");
			case "clean" -> eb.setTitle("New Clean Link Detected! (DENIED)");
			case "check" -> eb.setTitle("Link Marked (DENIED)");
			default -> Logger.log(Level.ERROR, "type unknown: " + approval.getType());
		}
		eb.setFooter("This link has been denied and reflected in the database.");
		eb.setColor(DiscordBot.COLOR_FROST);
		
		message.editMessage(eb.build()).queue(m -> {
			JsonDB.database.remove(approval, Approval.class);
		});
		
		// Set the domain to denied
		VTDomain vtdomain = JsonDBUtils.getVTDomainFromDomain(approval.getDomain());
		vtdomain.setAllowed(false);
		JsonDB.database.upsert(vtdomain);
		
		if ("clean".equals(approval.getType())) {
			try {
				DiscordBot.jda.getGuildById(DiscordBot.configuration.getGuildId())
				.getTextChannelById(approval.getMessageChannelId())
				.deleteMessageById(approval.getMessageId()).queue(m -> {
					DiscordUtils.sendMessage(approval.getMessageChannelId(), "Malicious link deleted!");
				});
			} catch (ErrorResponseException e) {
				Logger.log(Level.ERROR, "Message Error: " + e.toString());
			}
			
		}
	}
	
	private void handleAllow(Message message, Approval approval) {
		message.clearReactions().queue();
		
		EmbedBuilder eb = new EmbedBuilder(message.getEmbeds().get(0));
		switch (approval.getType()) {
			case "malicious" -> eb.setTitle("New Malicious Link Detected! (ALLOWED)");
			case "suspicious" ->  eb.setTitle("New Suspicious Link Detected! (ALLOWED)");
			case "clean" -> eb.setTitle("New Clean Link Detected! (ALLOWED)");
			case "check" -> eb.setTitle("Link Marked (ALLOWED)");
			default -> Logger.log(Level.ERROR, "type unknown: " + approval.getType());
		}
		eb.setFooter("This link has been allowed and reflected in the database.");
		eb.setColor(DiscordBot.COLOR_FROST);
		
		message.editMessage(eb.build()).queue(m -> {
			JsonDB.database.remove(approval, Approval.class);
		});
		
		// Set the domain to allowed
		VTDomain vtdomain = JsonDBUtils.getVTDomainFromDomain(approval.getDomain());
		vtdomain.setAllowed(true);
		JsonDB.database.upsert(vtdomain);
	}
}
