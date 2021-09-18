package bot.frostwall.xyz.discord;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscordConfiguration {
	
	private String appDirPath;

	private Map<String, String> bot;
	
	private Map<String, String> api;
	
	private Map<String, String> database;
	
	private String linkApprovalChannel;
	
	private String ownerId;
	
	private List<String> coOwnerIds;
	
	private String guildId;

	private Map<String, DiscordFeature> features;

	public Map<String, String> getBot() {
		return bot;
	}

	public void setBot(Map<String, String> bot) {
		this.bot = bot;
	}

	public Map<String, String> getApi() {
		return api;
	}

	public void setApi(Map<String, String> api) {
		this.api = api;
	}

	public Map<String, String> getDatabase() {
		return database;
	}

	public void setDatabase(Map<String, String> database) {
		this.database = database;
	}
	
	public String getLinkApprovalChannel() {
		return linkApprovalChannel;
	}
	
	public void setLinkApprovalChannel(String linkApprovalChannel) {
		this.linkApprovalChannel = linkApprovalChannel;
	}
	
	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public List<String> getCoOwnerIds() {
		return coOwnerIds;
	}

	public void setCoOwnerIds(List<String> coOwnerIds) {
		this.coOwnerIds = coOwnerIds;
	}

	public String getGuildId() {
		return guildId;
	}

	public void setGuildId(String guildId) {
		this.guildId = guildId;
	}

	public Map<String, DiscordFeature> getFeatures() {
		return features;
	}

	public void setFeatures(Map<String, DiscordFeature> features) {
		this.features = features;
	}

	
	
}
