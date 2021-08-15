package bot.frostwall.xyz.database;

import bot.frostwall.xyz.database.documents.Approval;
import bot.frostwall.xyz.database.documents.VTDomain;

public class JsonDBUtils {

	
	/**
	 * Fetch the VTDomain from the database.
	 * @param domain i.e "frostwall.xyz"
	 * @return VTDomain if it exists or null if not.
	 */
	public static VTDomain getVTDomainFromDomain(String domain) {
		if (domain == null) {
			return null;
		}
		
		VTDomain vtdomain = JsonDB.database.findById(domain, VTDomain.class);
		
		if (vtdomain != null) {
			return vtdomain;
		}
		
		return null;
	}
	
	public static Approval getApprovalFromMessageId(String messageId) {
		if (messageId == null) {
			return null;
		}
		
		Approval approval = JsonDB.database.findById(messageId, Approval.class);
		
		if (approval != null) {
			return approval;
		}
		
		return null;
	}
	
	
	
}