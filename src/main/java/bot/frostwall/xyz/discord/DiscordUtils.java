package bot.frostwall.xyz.discord;

import java.util.Map;

import com.jagrosh.jdautilities.command.CommandEvent;

import bot.frostwall.xyz.database.JsonDB;
import bot.frostwall.xyz.database.JsonDBUtils;
import bot.frostwall.xyz.database.documents.Approval;
import bot.frostwall.xyz.database.documents.VTDomain;
import bot.frostwall.xyz.logger.Logger;
import bot.frostwall.xyz.logger.Logger.Level;
import bot.frostwall.xyz.virustotal.VTUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DiscordUtils {

	public static void setBotStatus(String status) {
		DiscordBot.jda.getPresence().setActivity(Activity.of(ActivityType.WATCHING, status));
		DiscordBot.jda.getPresence().setStatus(OnlineStatus.IDLE);
	}
	
	
	
	public static void sendCleanMessage(MessageReceivedEvent event, VTDomain vtdomain, String link) {
		Logger.log(Level.SUCCESS, "New clean link detected!");
		
		Map<String, String> attrMap = vtdomain.getAttributes();
		String guildId = DiscordBot.configuration.getGuildId();
		String channelId = DiscordBot.configuration.getLinkApprovalChannel();
		
		EmbedBuilder eb = new EmbedBuilder()
				.setAuthor(event.getAuthor().getAsTag() + " (" + event.getAuthor().getId() + ")", null, event.getAuthor().getAvatarUrl())
				.setTitle("New Clean Link Detected! Human Approval Needed...")
				.setDescription("```\n" + event.getMessage().getContentRaw() + "\n```")
				.appendDescription(attrMap.get("malicious_stats") + " security vendor(s) flagged this domain as malicious.\n")
				.appendDescription(attrMap.get("suspicious_stats") + " security vendor(s) flagged this domain as suspicious.\n")
				.addField("Link", "`"+link+"`", true)
				.addField("# of Detections", vtdomain.getCount() + " times", true)
				.addBlankField(true)
				.addField("Flag Analysis", attrMap.get("analysis_stats"), true)
				.addField("Malicious", attrMap.get("malicious_stats"), true)
				.addField("Suspicious", attrMap.get("suspicious_stats"), true)
				.addField("Reputation", attrMap.get("reputation_votes"), true)
				.addField("Harmless Votes", attrMap.get("harmless_votes"), true)
				.addField("Malicious Votes", attrMap.get("malicious_votes"), true)
				.setFooter("React to allow or deny")
				.setColor(DiscordBot.COLOR_SUCCESS);
		
		
		DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).sendMessage(eb.build()).queue(m -> {
			
			Approval approval = new Approval();
			approval.setDiscordId(m.getId());
			approval.setMessageId(event.getMessageId());
			approval.setMessageChannelId(event.getChannel().getId());
			approval.setType("clean");
			approval.setContent(event.getMessage().getContentRaw());
			approval.setAttributes(attrMap);
			approval.setDomain(vtdomain.getDomain());
			JsonDB.database.upsert(approval);
			
			m.addReaction(DiscordBot.GREEN_TICK).queue();
			m.addReaction(DiscordBot.RED_TICK).queue();
		});
		
	}
	
	public static void sendMaliciousMessage(MessageReceivedEvent event, VTDomain vtdomain, String link) {
		Logger.log(Level.FATAL, "New malicious link detected!");
		
		Map<String, String> attrMap = vtdomain.getAttributes();
		String guildId = DiscordBot.configuration.getGuildId();
		String channelId = DiscordBot.configuration.getLinkApprovalChannel();
		
		EmbedBuilder eb = new EmbedBuilder()
				.setAuthor(event.getAuthor().getAsTag() + " (" + event.getAuthor().getId() + ")", null, event.getAuthor().getAvatarUrl())
				.setTitle("New Malicious Link Detected! Human Approval Needed...")
				.setDescription("```\n" + event.getMessage().getContentRaw() + "\n```")
				.appendDescription(attrMap.get("malicious_stats") + " security vendor(s) flagged this domain as malicious.\n")
				.appendDescription(attrMap.get("suspicious_stats") + " security vendor(s) flagged this domain as suspicious.\n")
				.addField("Link", "`"+link+"`", true)
				.addField("# of Detections", vtdomain.getCount() + " times", true)
				.addBlankField(true)
				.addField("Flag Analysis", attrMap.get("analysis_stats"), true)
				.addField("Malicious", attrMap.get("malicious_stats"), true)
				.addField("Suspicious", attrMap.get("suspicious_stats"), true)
				.addField("Reputation", attrMap.get("reputation_votes"), true)
				.addField("Harmless Votes", attrMap.get("harmless_votes"), true)
				.addField("Malicious Votes", attrMap.get("malicious_votes"), true)
				.setFooter("React to allow or deny")
				.setColor(DiscordBot.COLOR_FAILURE);
		
		
		DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).sendMessage(eb.build()).queue(m -> {
			
			Approval approval = new Approval();
			approval.setDiscordId(m.getId());
			approval.setMessageId(event.getMessageId());
			approval.setMessageChannelId(event.getChannel().getId());
			approval.setType("malicious");
			approval.setContent(event.getMessage().getContentRaw());
			approval.setAttributes(attrMap);
			approval.setDomain(vtdomain.getDomain());
			JsonDB.database.upsert(approval);
			
			m.addReaction(DiscordBot.GREEN_TICK).queue();
			m.addReaction(DiscordBot.RED_TICK).queue();
		});
		
	}
	
	public static void sendSuspiciousMessage(MessageReceivedEvent event, VTDomain vtdomain, String link) {
		Logger.log(Level.WARN, "New suspicious link detected!");
		
		Map<String, String> attrMap = vtdomain.getAttributes();
		String guildId = DiscordBot.configuration.getGuildId();
		String channelId = DiscordBot.configuration.getLinkApprovalChannel();
		
		EmbedBuilder eb = new EmbedBuilder()
				.setAuthor(event.getAuthor().getAsTag() + " (" + event.getAuthor().getId() + ")", null, event.getAuthor().getAvatarUrl())
				.setTitle("New Suspicious Link Detected! Human Approval Needed...")
				.setDescription("```\n" + event.getMessage().getContentRaw() + "\n```")
				.appendDescription(attrMap.get("malicious_stats") + " security vendor(s) flagged this domain as malicious.\n")
				.appendDescription(attrMap.get("suspicious_stats") + " security vendor(s) flagged this domain as suspicious.\n")
				.addField("Link", "`"+link+"`", true)
				.addField("# of Detections", vtdomain.getCount() + " times", true)
				.addBlankField(true)
				.addField("Flag Analysis", attrMap.get("analysis_stats"), true)
				.addField("Malicious", attrMap.get("malicious_stats"), true)
				.addField("Suspicious", attrMap.get("suspicious_stats"), true)
				.addField("Reputation", attrMap.get("reputation_votes"), true)
				.addField("Harmless Votes", attrMap.get("harmless_votes"), true)
				.addField("Malicious Votes", attrMap.get("malicious_votes"), true)
				.setFooter("React to allow or deny")
				.setColor(DiscordBot.COLOR_WARN);
		
		
		DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).sendMessage(eb.build()).queue(m -> {
			
			Approval approval = new Approval();
			approval.setDiscordId(m.getId());
			approval.setMessageId(event.getMessageId());
			approval.setMessageChannelId(event.getChannel().getId());
			approval.setType("suspicious");
			approval.setContent(event.getMessage().getContentRaw());
			approval.setAttributes(attrMap);
			approval.setDomain(vtdomain.getDomain());
			JsonDB.database.upsert(approval);
			
			m.addReaction(DiscordBot.GREEN_TICK).queue();
			m.addReaction(DiscordBot.RED_TICK).queue();
		});
		
	}
	
	public static void sendDeniedMessage(MessageReceivedEvent event, VTDomain vtdomain, String link) {
		Logger.log(Level.WARN, "Denied link detected! ");
		
		String guildId = DiscordBot.configuration.getGuildId();
		String channelId = DiscordBot.configuration.getLinkApprovalChannel();
		Map<String, String> attrMap = vtdomain.getAttributes();
		EmbedBuilder eb = new EmbedBuilder()
				.setAuthor(event.getAuthor().getAsTag() + " (" + event.getAuthor().getId() + ")", null, event.getAuthor().getAvatarUrl())
				.setTitle("Denied Link Detected!")
				.setDescription("```\n" + event.getMessage().getContentRaw() + "\n```")
				.appendDescription(attrMap.get("malicious_stats") + " security vendor(s) flagged this domain as malicious.\n")
				.appendDescription(attrMap.get("suspicious_stats") + " security vendor(s) flagged this domain as suspicious.\n")
				.addField("Link", "`"+link+"`", true)
				.addField("# of Detections", vtdomain.getCount() + " times", true)
				.addBlankField(true)
				.addField("Flag Analysis", attrMap.get("analysis_stats"), true)
				.addField("Malicious", attrMap.get("malicious_stats"), true)
				.addField("Suspicious", attrMap.get("suspicious_stats"), true)
				.addField("Reputation", attrMap.get("reputation_votes"), true)
				.addField("Harmless Votes", attrMap.get("harmless_votes"), true)
				.addField("Malicious Votes", attrMap.get("malicious_votes"), true)
				.setFooter("This is stored data. Use !f check <link> to rerun analysis and update data.")
				.setColor(DiscordBot.COLOR_FAILURE);
		
		DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).sendMessage(eb.build()).queue();
	}
	
	public static void sendCheckMessage(CommandEvent event, String domain,  String link) {
		String guildId = DiscordBot.configuration.getGuildId();
		String channelId = DiscordBot.configuration.getLinkApprovalChannel();
		VTDomain vtdomain = JsonDBUtils.getVTDomainFromDomain(domain);
		
		if (vtdomain == null) {
			vtdomain = new VTDomain();
			vtdomain.setDomain(domain);
			vtdomain.setAttributes(VTUtils.domainAttributeRequest(domain));
			vtdomain.setAllowed(false);
			
			JsonDB.database.upsert(vtdomain);
		}
		
		Map<String, String> attrMap = vtdomain.getAttributes();
		
		if (Boolean.valueOf(attrMap.get("error"))){
			event.reply("An HTTP request error occurred: Possibly rate limited.");
			return;
		}
		
		EmbedBuilder eb = new EmbedBuilder()
				.setDescription("```\n" + event.getMessage().getContentRaw() + "\n```")
				.appendDescription(attrMap.get("malicious_stats") + " security vendor(s) flagged this domain as malicious.\n")
				.appendDescription(attrMap.get("suspicious_stats") + " security vendor(s) flagged this domain as suspicious.\n")
				.addField("Link", "`"+link+"`", true)
				.addField("# of Detections", vtdomain.getCount() + " times", true)
				.addBlankField(true)
				.addField("Flag Analysis", attrMap.get("analysis_stats"), true)
				.addField("Malicious", attrMap.get("malicious_stats"), true)
				.addField("Suspicious", attrMap.get("suspicious_stats"), true)
				.addField("Reputation", attrMap.get("reputation_votes"), true)
				.addField("Harmless Votes", attrMap.get("harmless_votes"), true)
				.addField("Malicious Votes", attrMap.get("malicious_votes"), true)
				.setFooter("React to allow or deny");
				
		if (Integer.valueOf(attrMap.get("malicious_stats")) > 0 || Integer.valueOf(attrMap.get("suspicious_stats")) > 0) {
			 if (vtdomain.isAllowed()) {
				 eb.setTitle("Check: Malicious Link Detected! Marked (ALLOWED) in DB");
				 eb.setColor(DiscordBot.COLOR_FAILURE);
			 } else {
				 eb.setTitle("Check: Malicious Link Detected! Marked (DENIED) in DB");
				 eb.setColor(DiscordBot.COLOR_FAILURE);
			 }
		} else {
			if (vtdomain.isAllowed()) {
				 eb.setTitle("Check: Clean Link Detected! Marked (ALLOWED) in DB");
				 eb.setColor(DiscordBot.COLOR_SUCCESS);
			 } else {
				 eb.setTitle("Check: Clean Link Detected! Marked (DENIED) in DB");
				 eb.setColor(DiscordBot.COLOR_SUCCESS);
			 }
		}
		
		
		DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).sendMessage(eb.build()).queue(m -> {
			
			Approval approval = new Approval();
			approval.setDiscordId(m.getId());
			approval.setMessageId(event.getMessage().getId());
			approval.setMessageChannelId(event.getChannel().getId());
			approval.setType("check");
			approval.setContent(event.getMessage().getContentRaw());
			approval.setAttributes(attrMap);
			approval.setDomain(domain);
			JsonDB.database.upsert(approval);
			
			m.addReaction(DiscordBot.GREEN_TICK).queue();
			m.addReaction(DiscordBot.RED_TICK).queue();
		});
		
		event.getMessage().delete().queue();
		
	}
	
	public static void sendMessage(String channelId, String message) {
		
		String guildId = DiscordBot.configuration.getGuildId();
		
		DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).sendMessage(message).queue();
	}
	
	public static void sendMessage(String channelId, MessageEmbed embed) {
		
		String guildId = DiscordBot.configuration.getGuildId();
		
		DiscordBot.jda.getGuildById(guildId).getTextChannelById(channelId).sendMessage(embed).queue();
	}
}
