package bot.frostwall.xyz.database.documents;

import java.util.Map;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

@Document(collection = "domains", schemaVersion = "1.0")
public class VTDomain {

	@Id
	private String domain;
	private boolean isAllowed;
	private Map<String, String> attributes;
	/**
	 * Number of times detected.
	 */
	private int count;
	
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public boolean isAllowed() {
		return isAllowed;
	}
	
	public void setAllowed(boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
