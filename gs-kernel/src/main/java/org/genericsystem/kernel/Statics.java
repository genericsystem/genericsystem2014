package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class Statics {

	public static final int NO_POSITION = -1;
	public static final int BASE_POSITION = 0;
	public final static String ENGINE_VALUE = "Engine";
	public static final long MILLI_TO_NANOSECONDS = 1000000L;

	public static <T> Stream<T> concat(Stream<T>[] array) {
		return Arrays.stream(array).flatMap(x -> x);
	}

	public static <T, U> Stream<T> concat(Stream<U> stream, Function<U, Stream<T>> mappers) {
		return stream.<Stream<T>> map(mappers).flatMap(x -> x);
		// return stream.<Stream<Vertex>> map(mappers).reduce(Stream.empty(), Stream::concat)
	}
}