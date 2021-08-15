package bot.frostwall.xyz.virustotal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import bot.frostwall.xyz.discord.DiscordBot;
import bot.frostwall.xyz.logger.Logger;
import bot.frostwall.xyz.logger.Logger.Level;

public class VTUtils {

	
	
	public static Map<String, String> domainAttributeRequest(String domain) {
		Map<String, String> attrMap = new HashMap<>();
		attrMap.put("error", "false");
		try {
			Document doc = Jsoup.connect("https://www.virustotal.com/api/v3/domains/" + domain)
					.ignoreContentType(true)
					.header("x-apikey", DiscordBot.configuration.getApi().get("virus_total_api_key"))
					.get();
			
			
			attrMap.put("domain", domain);
			
			ObjectMapper mapper = new ObjectMapper();
			
			JsonNode payloadNode = mapper.readTree(doc.text()).get("data");
			JsonNode attributesNode = payloadNode.get("attributes");
			
			attrMap.put("reputation_votes", attributesNode.get("reputation").asText());
			attrMap.put("harmless_votes", attributesNode.get("total_votes").get("harmless").asText());
			attrMap.put("malicious_votes", attributesNode.get("total_votes").get("malicious").asText());
			
			
			JsonNode stats = attributesNode.get("last_analysis_stats");
			int a = stats.get("harmless").asInt();
			int b = stats.get("malicious").asInt();
			int c = stats.get("suspicious").asInt();
			int d = stats.get("undetected").asInt();
			int e = stats.get("timeout").asInt();
			int total = a + b + c + d + e;
			
			attrMap.put("harmless_stats", a + "");
			attrMap.put("malicious_stats", b + "");
			attrMap.put("suspicious_stats", c + "");
			attrMap.put("analysis_stats", b + "/" + total);
			
			
			
		} catch (IOException e) {
			Logger.log(Level.ERROR, "Exception occurred while making endpoint request: " + e.toString());
			//run alternative in case auth error or rate limit.
			attrMap.put("error", "true");
		}
		
		
		return attrMap;
	}
	
	
	
}
