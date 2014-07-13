package tuwien.big.formel0.highscore.api;

import javax.xml.ws.WebFault;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.2.4-b01 Generated
 * source version: 2.2
 *
 */
@WebFault(name = "Failure", targetNamespace = "http://big.tuwien.ac.at/we/highscore")
public class Failure
        extends Exception {

    /**
     * Java type that goes as soapenv:Fault detail element.
     *
     */
    private FailureType faultInfo;

    /**
     *
     * @param message
     * @param faultInfo
     */
    public Failure(String message, FailureType faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     *
     * @param message
     * @param faultInfo
     * @param cause
     */
    public Failure(String message, FailureType faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     *
     * @return returns fault bean: tuwien.big.formel0.highscore.api.FailureType
     */
    public FailureType getFaultInfo() {
        return faultInfo;
    }
}
