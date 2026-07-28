package unl.edu.cc.rest.jbrew.exception.handler;

import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerFactory;

public class AjaxDomainExceptionHandlerFactory extends ExceptionHandlerFactory {

    public AjaxDomainExceptionHandlerFactory(ExceptionHandlerFactory wrapped) {
        super(wrapped);
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return new AjaxDomainExceptionHandler(getWrapped().getExceptionHandler());
    }
}