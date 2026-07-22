package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.exception.CredentialInvalidException;
import java.util.logging.Logger;

@Stateless
public class SecurityFacade {
    
    private static final Logger LOGGER = Logger.getLogger(SecurityFacade.class.getName());
    
    @Inject
    private AuthenticationService authenticationService;
    
    public AuthenticationService.AuthenticationResult authenticate(String username, String password) {
        LOGGER.info("Autenticando usuario a través de facade: " + username);
        try {
            return authenticationService.authenticate(username, password);
        } catch (CredentialInvalidException e) {
            LOGGER.warning("Autenticación fallida a través de facade: " + e.getMessage());
            throw e;
        }
    }
}
