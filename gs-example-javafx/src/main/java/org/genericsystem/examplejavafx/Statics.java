package org.genericsystem.examplejavafx;

import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class Statics {
	@SuppressWarnings("unchecked")
	static <T> StringConverter<T> getDefaultConverter(Class<T> clazz){
		if(Integer.class.equals(clazz))
			return (StringConverter<T>) new IntegerStringConverter();
		return (StringConverter<T>) new DefaultStringConverter();
	}
}
