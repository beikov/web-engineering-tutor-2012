package at.ac.big.tuwien.ewa.util;

import java.util.HashMap;
import java.util.Map;


/**
 * Class holding the different error codes which may be returned by the highscore service
 * @author liegl
 *
 */
public abstract class ServiceErrorCodes {
	
	/**
	 * 1xx Error COdes
	 * Access denied error code 
	 */
	public static final String ACCESS_DENIED = "100";
	
	
	/**
	 * 2xx Error Codes
	 * Incomplete data error code 
	 */
	public static final String INCOMPLETE_OR_INVALID_DATA = "200";
	
		
	/**
	 * Non schema conformant request
	 */
	public static final String NON_SCHEMA_CONFORMANT_REQUEST = "201";
	
	/**
	 * 3xx Error Codes
	 * Internal server error error code
	 */
	public static final String INTERNAL_SERVER_ERROR = "300";
	
	/**
	 * A map holding the error code descriptions	
	 */
	private static final Map<String, String> errorCodeDescriptions;
	
	/**
	 * Initialize the map
	 */
	static {
		errorCodeDescriptions = new HashMap<String, String>();
		errorCodeDescriptions.put(ACCESS_DENIED, "ACCESS_DENIED");
		errorCodeDescriptions.put(INCOMPLETE_OR_INVALID_DATA, "INCOMPLETE_OR_INVALID_DATA");		
		errorCodeDescriptions.put(NON_SCHEMA_CONFORMANT_REQUEST, "NON_SCHEMA_CONFORMANT_REQUEST");
		errorCodeDescriptions.put(INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
		
	}
	
	
	/**
	 * Get the error code description
	 * @param code
	 * @return
	 */
	public static String getErrorCodeDescription(String code) {
		return errorCodeDescriptions.get(code);
	}

}
