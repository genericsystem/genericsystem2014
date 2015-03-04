package org.genericsystem.issuetracker.config;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyExceptionHandler extends ExceptionHandlerWrapper {

	private static final Logger log = LoggerFactory.getLogger(MyExceptionHandler.class);
	private ExceptionHandler exceptionHandler;
	private UIComponent component;

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
			if (throwable instanceof Throwable) {
				Throwable t = throwable;
				FacesContext facesContext = FacesContext.getCurrentInstance();
				try {
					String message = null;
					if (t != null && t.getCause() != null) {
						message = t.getCause().toString();
					}
					facesContext.addMessage("msg", new FacesMessage(message));
				} finally {
					it.remove();
				}
			}
		}
		getWrapped().handle();
	}

	public UIComponent getComponent() {
		return component;
	}

	public void setComponent(UIComponent component) {
		this.component = component;
	}

}
