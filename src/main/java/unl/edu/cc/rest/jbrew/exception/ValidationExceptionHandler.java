package unl.edu.cc.rest.jbrew.exception;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ExceptionQueuedEvent;
import jakarta.faces.event.ExceptionQueuedEventContext;
import unl.edu.cc.rest.jbrew.domain.Exception.InvalidProductNameException;
import unl.edu.cc.rest.jbrew.domain.Exception.InvalidProductPriceException;
import unl.edu.cc.rest.jbrew.domain.Exception.InvalidProductStockException;

import java.util.Iterator;

public class ValidationExceptionHandler extends ExceptionHandlerWrapper {

    public ValidationExceptionHandler(ExceptionHandler wrapped) {
        super(wrapped);
    }

    @Override
    public void handle() {
        Iterator<ExceptionQueuedEvent> eventos = getUnhandledExceptionQueuedEvents().iterator();

        while (eventos.hasNext()) {
            ExceptionQueuedEvent evento = eventos.next();
            ExceptionQueuedEventContext contexto = (ExceptionQueuedEventContext) evento.getSource();
            Throwable causaDeValidacion = buscarCausaDeValidacion(contexto.getException());

            if (causaDeValidacion != null) {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Dato inválido", causaDeValidacion.getMessage()));
                facesContext.validationFailed();
                facesContext.renderResponse(); // se queda en la misma vista, sin navegar a una página de error
                eventos.remove(); // ya se manejó: no debe llegar al manejador de errores por defecto
            }
        }

        // Cualquier otra excepción no reconocida sigue su curso normal
        super.handle();
    }

    private Throwable buscarCausaDeValidacion(Throwable excepcion) {
        Throwable actual = excepcion;
        while (actual != null) {
            if (esExcepcionDeValidacion(actual)) {
                return actual;
            }
            actual = actual.getCause();
        }
        return null;
    }

    private boolean esExcepcionDeValidacion(Throwable excepcion) {
        return excepcion instanceof InvalidProductNameException
                || excepcion instanceof InvalidProductPriceException
                || excepcion instanceof InvalidProductStockException;
    }
}