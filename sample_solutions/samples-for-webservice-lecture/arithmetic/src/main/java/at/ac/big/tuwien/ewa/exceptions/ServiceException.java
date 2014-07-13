package at.ac.big.tuwien.ewa.exceptions;

import javax.xml.ws.WebFault;

@WebFault(name = "ServiceException", targetNamespace = "http://www.example.com")
public class ServiceException extends Exception {


    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
