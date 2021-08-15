package bot.frostwall.xyz.database.documents;

import java.util.Map;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "approvals", schemaVersion = "1.0")
public class Approval {

	@Id
	private String discordId;
	private String messageId;
	private String messageChannelId;
	/**
	 * Types:
	 * "malicious" - the embed is for when the analysis comes back as malicious and the embed needs approval.
	 * "suspicious" - the embed is for when the analysis comes back as suspicious and the embed needs approval.
	 * "clean" - the embed is for when the analysis comes back clean but needs approval to be added to the database (human check).
	 * "check" - the embed is for checking the status of a domain in the database and allows you to update if wanted.
	 * 
	 */
	private String type;
	private String content;
	private String domain;
	
	private Map<String, String> attributes;
	
	public String getDiscordId() {
		return discordId;
	}
	
	public void setDiscordId(String discordId) {
		this.discordId = discordId;
	}
	
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public String getMessageChannelId() {
		return messageChannelId;
	}

	public void setMessageChannelId(String messageChannelId) {
		this.messageChannelId = messageChannelId;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	

	
}
