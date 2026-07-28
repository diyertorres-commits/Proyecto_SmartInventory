package unl.edu.cc.rest.jbrew.exception.handler;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ExceptionQueuedEvent;
import jakarta.faces.event.ExceptionQueuedEventContext;

import java.util.Iterator;

public class AjaxDomainExceptionHandler extends ExceptionHandlerWrapper {

    private static final String PAQUETE_EXCEPCIONES_DOMINIO = "unl.edu.cc.rest.jbrew.domain.Exception";

    public AjaxDomainExceptionHandler(ExceptionHandler wrapped) {
        super(wrapped);
    }

    @Override
    public void handle() {
        Iterator<ExceptionQueuedEvent> eventos = getUnhandledExceptionQueuedEvents().iterator();

        while (eventos.hasNext()) {
            ExceptionQueuedEventContext contexto = (ExceptionQueuedEventContext) eventos.next().getSource();
            Throwable causaRaiz = raizDe(contexto.getException());

            if (esExcepcionDeDominio(causaRaiz)) {
                mostrarComoMensajeDeUsuario(causaRaiz);
                eventos.remove(); // ya la manejamos: que no siga como error sin controlar
            }
        }

        getWrapped().handle();
    }

    private void mostrarComoMensajeDeUsuario(Throwable causaRaiz) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de validación", causaRaiz.getMessage()));
        facesContext.validationFailed();
        facesContext.renderResponse();
    }

    private Throwable raizDe(Throwable excepcion) {
        Throwable causa = excepcion;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        return causa;
    }

    private boolean esExcepcionDeDominio(Throwable excepcion) {
        Package paquete = excepcion.getClass().getPackage();
        return paquete != null && PAQUETE_EXCEPCIONES_DOMINIO.equals(paquete.getName());
    }
}