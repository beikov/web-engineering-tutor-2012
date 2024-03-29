package at.ac.big.tuwien.ewa.ws;

import java.math.BigDecimal;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.big.tuwien.ewa.exceptions.ServiceException;


/**
 * Sample Web Service
 * @author pl
 *
 */
@WebService(name = "ArithmeticService")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
public class ArithmeticService {
	
	
	/** Define a logger */
	private static final Logger log = LoggerFactory.getLogger(ArithmeticService.class);
	
	static {
		System.setProperty("com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace", "true");
	}

	/**
	 * Simple function for adding two parameters
	 * @param addend_a
	 * @param addend_b
	 * @return
	 * @throws ServiceException
	 */
	@WebMethod(operationName = "addFunction")
	@WebResult(name = "sum")
	public BigDecimal add(@WebParam(name="Addend_A") BigDecimal addend_a, @WebParam(name="Addend_B")BigDecimal addend_b) throws ServiceException {
		
		log.debug("Received add request with addend {} and addend {}", new Object[]{addend_a, addend_b});
		
		if (addend_a == null || addend_b == null) {
			log.debug("Received invalid request for add function");
			throw (new ServiceException("Parameter invalid"));
		}
						
		return addend_a.add(addend_b);
	}

	/**
	 * Simple function for subtracting two values
	 * @param minuend
	 * @param subtrahend
	 * @return
	 * @throws ServiceException
	 */
	@WebMethod(operationName = "subtractFunction")
	@WebResult(name = "difference")
	public BigDecimal subtract(@WebParam(name="Minuend")BigDecimal minuend, @WebParam(name="Subtrahend")BigDecimal subtrahend) throws ServiceException {
		
		log.debug("Received subtract request with minuend {} and subtrahend {}", new Object[]{minuend, subtrahend});
		
		if (minuend == null || subtrahend == null)  {
			log.debug("Received invalid request for substract function");
			throw (new ServiceException("Parameter invalid"));
		}
				
		return minuend.subtract(subtrahend);
	}


	/**
	 * Determines if value is even/odd and positive/negative
	 * @param input
	 * @return
	 * @throws ServiceException
	 */
	@WebMethod(operationName = "whoAmI")
	@WebResult(name = "evaluationResult")
	public String whoAmI(@WebParam(name="inputInteger") BigDecimal input) throws ServiceException {
		StringBuffer sb = new StringBuffer();
	
		if (input == null) {
			log.debug("Received invalid request for evaluation function");
			throw (new ServiceException("Parameter invalid"));
		}
		
		log.debug("Received whoAmI request with parameter {}", input);
		
		sb.append("The passed value ").append(input.toPlainString()).append(" is ");
		
		//Determine if even or odd
		if (isEven(input)) {			
			sb.append("even");
		}
		else {
			sb.append("odd");
		}
		
		sb.append(" and ");
		//Determine if positive or negative
		if (isNegative(input)) {
			sb.append("negative");
		}
		else {
			sb.append("positive");
		}
		
		
		
				
		return sb.toString();
	}

	
	/**
	 * Determine if negative or positive
	 * @param d
	 * @return
	 */
	private boolean isNegative(BigDecimal d) {
		if (d.intValue() < 0) {
			return true;
		}
		
		return false;
	}
	
	
	
	/**
	 * Determine if even or odd
	 * @param d
	 * @return
	 */
	private boolean isEven(BigDecimal d) {
		
		if (d.intValue() % 2 == 0) {
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * Invoker
	 * @param args
	 */
	public static void main(String[] args) {
		log.debug("Starting Arithmetic Web Service.");
		Endpoint endpoint = Endpoint.publish(
				"http://localhost:8080/arithmeticservice",
				new ArithmeticService());
	}

}
