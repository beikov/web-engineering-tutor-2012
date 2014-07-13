package at.ac.big.tuwien.ewa.ws.handler;


import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.developer.ValidationErrorHandler;

/**
 * This class is used to appropriately react on invalid instances which might be
 * passed to the web service
 * 
 * @author liegl
 * 
 */
public class SchemaValidationErrorHandler extends ValidationErrorHandler {

	/**
	 * Denotes a validation warning 
	 */
	public static final String WARNING = "SchemaValidationWarning";
	
	/**
	 * Denotes a validation error 
	 */
	public static final String ERROR = "SchemaValidationError";
	
	/**
	 * Denotes a validation fatal error
	 */
	public static final String FATAL_ERROR = "SchemaValidationFatalError";

	/**
	 * React on validation warning - store the exception in the packet so that we can retrieve
	 * it at the endpoint
	 */
	@Override
	public void warning(SAXParseException exception) throws SAXException {
		packet.invocationProperties.put(WARNING, exception);

	}

	/**
	 * React on validation errors - store the exception in the packet so that we can retrieve
	 * it at the endpoint
	 */
	@Override
	public void error(SAXParseException exception) throws SAXException {
		packet.invocationProperties.put(ERROR, exception);

	}
	
	

	/**
	 * React on validation fatal errors - store the exception in the packet so that we can retrieve
	 * it at the endpoint
	 */
	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		packet.invocationProperties.put(FATAL_ERROR, exception);

	}

}
