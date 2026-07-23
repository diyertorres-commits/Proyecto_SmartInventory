package unl.edu.cc.rest.jbrew.exception;

public class CredentialInvalidException extends RuntimeException {
    
    public CredentialInvalidException(String message) {
        super(message);
    }
    
    public CredentialInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
