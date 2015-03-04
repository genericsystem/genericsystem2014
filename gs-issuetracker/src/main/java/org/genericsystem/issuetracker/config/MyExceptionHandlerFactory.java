package org.genericsystem.issuetracker.config;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class MyExceptionHandlerFactory extends ExceptionHandlerFactory {

	private ExceptionHandlerFactory exceptionHandlerFactory;

	public MyExceptionHandlerFactory(final javax.faces.context.ExceptionHandlerFactory parent) {
		this.exceptionHandlerFactory = parent;
	}

	@Override
	public ExceptionHandler getExceptionHandler() {
		ExceptionHandler result = exceptionHandlerFactory.getExceptionHandler();
		result = new MyExceptionHandler(result);
		return result;
	}

}
