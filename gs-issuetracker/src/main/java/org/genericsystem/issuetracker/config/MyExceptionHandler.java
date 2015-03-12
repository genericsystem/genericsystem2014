package org.genericsystem.issuetracker.config;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

public class MyExceptionHandler extends ExceptionHandlerWrapper {

	private ExceptionHandler exceptionHandler;

	public MyExceptionHandler(ExceptionHandler wrapped) {
		this.exceptionHandler = wrapped;
	}

	@Override
	public javax.faces.context.ExceptionHandler getWrapped() {
		return this.exceptionHandler;
	}

	@Override
	public void handle() throws FacesException {
		for (Iterator<ExceptionQueuedEvent> it = getUnhandledExceptionQueuedEvents().iterator(); it.hasNext();) {
			ExceptionQueuedEvent exceptionQueuedEvent = it.next();
			ExceptionQueuedEventContext exceptionQueuedEventContext = (ExceptionQueuedEventContext) exceptionQueuedEvent.getSource();
			Throwable throwable = exceptionQueuedEventContext.getException();
			FacesContext facesContext = FacesContext.getCurrentInstance();
			try {
				String message = null;
				if (throwable != null && throwable.getCause() != null) {
					message = throwable.getCause().toString();
				}
				facesContext.addMessage("msg", new FacesMessage(message));
			} finally {
				it.remove();
			}
		}
		getWrapped().handle();
	}

}
