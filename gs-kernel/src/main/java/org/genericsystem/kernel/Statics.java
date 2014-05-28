package org.genericsystem.kernel;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Statics {

	private static Logger log = LoggerFactory.getLogger(Statics.class);

	private static ThreadLocal<Long> threadDebugged = new ThreadLocal<Long>();

	public static final int NO_POSITION = -1;
	public static final int BASE_POSITION = 0;
	public final static String ENGINE_VALUE = "Engine";
	public static final long MILLI_TO_NANOSECONDS = 1000000L;

	public static final int ATTEMPT_SLEEP = 15;
	public static final int ATTEMPTS = 50;

	public static <T> Stream<T> concat(Stream<T>[] array) {
		return Arrays.stream(array).flatMap(x -> x);
	}

	public static <T, U> Stream<T> concat(Stream<U> stream, Function<U, Stream<T>> mappers) {
		return stream.<Stream<T>> map(mappers).flatMap(x -> x);
		// return stream.<Stream<Vertex>> map(mappers).reduce(Stream.empty(), Stream::concat)
	}

	public static Vertex[] insertIntoArray(Vertex generic, Vertex[] targets, int basePos) {
		if (basePos < 0 || basePos > targets.length)
			throw new IllegalStateException("Unable to find a valid base position");
		Vertex[] result = new Vertex[targets.length + 1];
		System.arraycopy(targets, 0, result, 0, basePos);
		result[basePos] = generic;
		System.arraycopy(targets, basePos, result, basePos + 1, result.length - basePos - 1);
		return result;
	}

	public static void debugCurrentThread() {
		threadDebugged.set(System.currentTimeMillis());
	}

	public static void stopDebugCurrentThread() {
		threadDebugged.remove();
	}

	public static boolean isCurrentThreadDebugged() {
		return threadDebugged.get() != null;
	}

	public static void logTimeIfCurrentThreadDebugged(String message) {
		if (isCurrentThreadDebugged())
			log.info(message + " : " + (System.currentTimeMillis() - threadDebugged.get()));
	}

}
