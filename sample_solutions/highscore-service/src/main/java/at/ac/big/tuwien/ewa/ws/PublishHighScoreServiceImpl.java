package at.ac.big.tuwien.ewa.ws;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

import at.ac.big.tuwien.ewa.persistence.DataStore;
import at.ac.big.tuwien.ewa.persistence.DataStoreEntry;
import at.ac.big.tuwien.ewa.util.ServiceErrorCodes;
import at.ac.big.tuwien.ewa.ws.generated.Failure;
import at.ac.big.tuwien.ewa.ws.generated.FailureType;
import at.ac.big.tuwien.ewa.ws.generated.GameType;
import at.ac.big.tuwien.ewa.ws.generated.HighScoreRequestType;
import at.ac.big.tuwien.ewa.ws.generated.PublishHighScoreEndpoint;
import at.ac.big.tuwien.ewa.ws.generated.TournamentType;
import at.ac.big.tuwien.ewa.ws.generated.TournamentType.Players.Player;
import at.ac.big.tuwien.ewa.ws.generated.TournamentType.Rounds.Round;
import at.ac.big.tuwien.ewa.ws.handler.SchemaValidationErrorHandler;

import com.sun.xml.ws.developer.SchemaValidation;

/**
 * Highscore service for publishing information to the highscore service board
 * 
 * @author pl
 * 
 */
@WebService(serviceName = "PublishHighScoreService", portName = "PublishHighScorePort", targetNamespace = "http://big.tuwien.ac.at/we/highscore", endpointInterface = "at.ac.big.tuwien.ewa.ws.generated.PublishHighScoreEndpoint", wsdlLocation = "WEB-INF/wsdl/wehighscore.wsdl")
@SchemaValidation(handler = SchemaValidationErrorHandler.class)
public class PublishHighScoreServiceImpl implements PublishHighScoreEndpoint {

	protected final Logger LOG = LoggerFactory.getLogger(PublishHighScoreServiceImpl.class.getName());

	/**
	 * The Web Service context - used to get the message context and the passed
	 * username/password
	 */
	@Resource
	public WebServiceContext wsctx;

	private final String userKey = "34EphAp2C4ebaswu";

	// Make sure, that the service does not return any internal stack traces
	static {
		System.setProperty(
				"com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace",
				"true");
		Locale.setDefault(Locale.US);

	}

	@Override
	public String publishHighScore(HighScoreRequestType in) throws Failure {

		// Check XML Schema conformance
		checkSchemaConformance(wsctx.getMessageContext());

		// Check if the user key matches the required key
		String userKey = in.getUserKey() == null ? "" : in.getUserKey();
		if (!userKey.equals(this.userKey)) {
			FailureType ft = new FailureType();
			ft.setCode(ServiceErrorCodes.ACCESS_DENIED);
			ft.setReason(ServiceErrorCodes
					.getErrorCodeDescription(ServiceErrorCodes.ACCESS_DENIED));
			ft.setDetail("Invalid user key. You must provide a correct user key in order to access this service.");
			throw new Failure("Invalid user key.", ft);
		}

		// Check if the necessary elements are present
		TournamentType t = in.getTournament();
		performIntegrityChecks(t);

		// Generate a UUID which we return to the user
		UUID generatedId = UUID.randomUUID();

		DataStoreEntry entry = new DataStoreEntry();
		entry.setId(generatedId.toString());
		entry.setSubmitted(new DateTime(new Date()));
		entry.setTournament(in.getTournament());

		// Store the entry in the data store
		DataStore.getInstance().addEntry(entry);
		
		// Log the successful storage
		LOG.info("Successfully stored entry with UUID "
				+ generatedId.toString());

		//Some debug noise
		if (LOG.isDebugEnabled()) {
			LOG.debug("Currently holding the following set of {} entries:", DataStore.getInstance().getEntries().size());
			for (DataStoreEntry entry1 : DataStore.getInstance().getEntries()) {
				LOG.debug("Entry: {}", entry1);
			}	
		}
		
		return generatedId.toString();
	}

	
	
	/**
	 * Used to check whether the submitted data is complete
	 * 
	 * @param TournamentType t
	 * @throws Failure
	 */
	private void performIntegrityChecks(TournamentType t) throws Failure {
		
		//Start date attribute must be present
		if (t.getStartDate() == null) {
			generateException("You must provide an 'start-date' attribute.");
		}
		
		//End date attribute must be present
		if (t.getEndDate() == null) {
			generateException("You must provide an 'end-date' attribute.");
		}
		
		//Registration-deadline must be present
		if (t.getRegistrationDeadline() == null) {
			generateException("You must provide an 'registration-deadline' attribute.");
		}
		
		
		//There must be exactly one player
		if (t.getPlayers() == null || t.getPlayers().getPlayer() == null || t.getPlayers().getPlayer().size() != 1){
			generateException("You must provide exactly one 'player' element.");
		}
		
		//Get the passed player
		Player p = t.getPlayers().getPlayer().get(0);
		
		//Username attribute of player must be present
		if (StringUtils.isEmpty(p.getUsername()))  {
			generateException("You must provide a username attribute for the 'player' element.");
		}
		
		//Date of birth must be present
		if (p.getDateOfBirth() == null) {
			generateException("You must provide a 'date-of-birth' element.");
		}
		
		//Gender must be present
		if (StringUtils.isEmpty(p.getGender())) {
			generateException("You must provide a 'gender' element.");
		}
		
		//Gender must be MALE of FEMALE
		if (!(p.getGender().equals("MALE") || p.getGender().equals("FEMALE"))) {
			generateException("'gender' must be either MALE or FEMALE");
		}
		
		//There must be exactly one round
		if (t.getRounds() == null || t.getRounds().getRound() == null || t.getRounds().getRound().size() != 1) {
			generateException("You must provide exactly one 'round' element.");
		}
		
		//The round must have the number 0
		Round r = t.getRounds().getRound().get(0);
		if (r.getNumber() != 0) {
			generateException("'number' attribute of 'round' element must be 0.");
		}
		
		//Round must contain exactly one game
		if (r.getGame() == null || r.getGame().size() != 1) {
			generateException("You must provide exactly one 'game' element.");
		}
		
		GameType game = r.getGame().get(0);
		
		//Date must be set
		if (game.getDate() == null) {
			generateException("You must provide a 'date' attribute.");
		}
		
		//Status must be finished
		if (StringUtils.isEmpty(game.getStatus()) || !game.getStatus().equals("finished")) {
			generateException("'status' attribute must be present and have the value 'finished'.");
		}
		
		//Duration must be present
		if (game.getDuration() == null) {
			generateException("You must provide a 'duration' attribute.");
		}
		
		//Winner must be present
		if (StringUtils.isEmpty(game.getWinner())) {
			generateException("You must provide a 'winner' attribute.");
		}
		

	}
	
	
	/**
	 * Used to throw a failure for incomplete data
	 * @param message
	 * @throws Failure
	 */
	private void generateException(String message) throws Failure {
		FailureType ft = new FailureType();
		ft.setCode(ServiceErrorCodes.INCOMPLETE_OR_INVALID_DATA);
		ft.setReason(ServiceErrorCodes
				.getErrorCodeDescription(ServiceErrorCodes.INCOMPLETE_OR_INVALID_DATA));
		ft.setDetail(message);
		throw new Failure("Incomplete data.", ft);		
	}

	/**
	 * Check if any schema validation errors have occurred - in case there are
	 * some - return a customized Failure
	 * 
	 * @param mctx
	 * @throws Failure
	 */
	private void checkSchemaConformance(MessageContext messageContext)
			throws Failure {

		// We'll ignore warnings
		// SAXParseException warningException =
		// (SAXParseException)messageContext.get(SchemaValidationErrorHandler.WARNING);
		SAXParseException errorException = (SAXParseException) messageContext
				.get(SchemaValidationErrorHandler.ERROR);
		SAXParseException fatalErrorException = (SAXParseException) messageContext
				.get(SchemaValidationErrorHandler.FATAL_ERROR);

		// Is there an error exception?
		String errorMessage = null;
		if (errorException != null) {
			errorMessage = errorException.getMessage();
		} else if (fatalErrorException != null) {
			errorMessage = fatalErrorException.getMessage();
		}

		// There is an error message - throw a Failure
		if (!StringUtils.isEmpty(errorMessage)) {

			FailureType ft = new FailureType();
			ft.setCode(ServiceErrorCodes.NON_SCHEMA_CONFORMANT_REQUEST);
			ft.setReason(ServiceErrorCodes
					.getErrorCodeDescription(ServiceErrorCodes.NON_SCHEMA_CONFORMANT_REQUEST));
			ft.setDetail("Your request is not conformant to the XML Schema, referenced by the WSDL. Errordetail: " + errorMessage);

			throw new Failure("Request is not schema conformant.", ft);
		}

	}

}
