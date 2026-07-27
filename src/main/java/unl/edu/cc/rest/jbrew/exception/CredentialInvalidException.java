package unl.edu.cc.rest.jbrew.exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class CredentialInvalidException extends RuntimeException {

    public CredentialInvalidException(String message) {
        super(message);
    }

    public CredentialInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}