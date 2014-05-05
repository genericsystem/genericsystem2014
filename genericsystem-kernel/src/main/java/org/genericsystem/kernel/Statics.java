package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class Statics {

	public static final int NO_POSITION = -1;
	public static final int BASE_POSITION = 0;
	public final static String ENGINE_VALUE = "Engine";
	public static final Vertex[] EMPTY_VERTICES = new Vertex[] {};

	public static final int ATTEMPT_SLEEP = 15;
	public static final int ATTEMPTS = 50;

	public static final long MILLI_TO_NANOSECONDS = 1000000L;
	public static final long LIFE_TIMEOUT = 1386174608777L;// 30 minutes

	public static Stream<Vertex> concat(Stream<Vertex>[] array) {
		return Arrays.stream(array).flatMap(x -> x);
	}

	public static Stream<Vertex> concat(Stream<Vertex> stream, Function<Vertex, Stream<Vertex>> mappers) {
		return stream.<Stream<Vertex>> map(mappers).flatMap(x -> x);
		// return stream.<Stream<Vertex>> map(mappers).reduce(Stream.empty(), Stream::concat)
	}
}