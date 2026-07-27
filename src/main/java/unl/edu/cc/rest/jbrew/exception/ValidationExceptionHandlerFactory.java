package unl.edu.cc.rest.jbrew.exception;

import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerFactory;

public class ValidationExceptionHandlerFactory extends ExceptionHandlerFactory {

    private final ExceptionHandlerFactory wrapped;

    public ValidationExceptionHandlerFactory(ExceptionHandlerFactory wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return new ValidationExceptionHandler(wrapped.getExceptionHandler());
    }
}