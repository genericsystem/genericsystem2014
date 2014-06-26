package org.genericsystem.impl;

import org.genericsystem.api.core.Generic;
import org.genericsystem.kernel.services.RemovableService.RemoveStrategy;

public class Statics {

	public static final Generic[] EMPTY_GENERIC_ARRAY = new Generic[] {};
	public static final String ROOT_NODE_VALUE = "Engine";

	public static final int META = 0;
	public static final int STRUCTURAL = 1;
	public static final int CONCRETE = 2;
	public static final int SENSOR = 3;

	public static String getMetaLevelString(int metaLevel) {
		switch (metaLevel) {
		case META:
			return "META";
		case STRUCTURAL:
			return "STRUCTURAL";
		case CONCRETE:
			return "CONCRETE";
		case SENSOR:
			return "SENSOR";
		default:
			return "UNKNOWN";
		}
	}

	public static final int MULTIDIRECTIONAL = -1;
	public static final int BASE_POSITION = 0;
	public static final int TARGET_POSITION = 1;
	public static final int SECOND_TARGET_POSITION = 2;

	public static final int TYPE_SIZE = 0;
	public static final int ATTRIBUTE_SIZE = 1;
	public static final int RELATION_SIZE = 2;
	public static final int TERNARY_RELATION_SIZE = 3;

	public static final int ATTEMPT_SLEEP = 15; // ms
	public static final int ATTEMPTS = 50;

	public static final String GS_EXTENSION = ".gs";
	public static final String FORMAL_EXTENSION = ".formal";
	public static final String CONTENT_EXTENSION = ".content";
	public static final String PART_EXTENSION = ".part";
	public static final String LOG_PATTERN = "yyyy.MM.dd  HH:mm:ss  SSSS";
	public static final String PATTERN = "yyyy.MM.dd_HH-mm-ss.SSS";
	public static final String MATCHING_REGEX = "[0-9]{4}.[0-9]{2}.[0-9]{2}_[0-9]{2}-[0-9]{2}-[0-9]{2}.[0-9]{3}---[0-9]+";
	public static final String LOCK_FILE_NAME = ".lock";
	public static final String ZIP_EXTENSION = GS_EXTENSION + ".zip";
	public static final long MILLI_TO_NANOSECONDS = 1000000L;
	public static final long ARCHIVER_COEFF = 5L;
	public static final long SNAPSHOTS_PERIOD = 1000L;
	public static final long SNAPSHOTS_INITIAL_DELAY = 1000L;
	public static final long GARBAGE_PERIOD = 1000L;
	public static final long GARBAGE_INITIAL_DELAY = 1000L;
	public static final long LIFE_TIMEOUT = 1386174608777L;// 30 minutes

	public static RemoveStrategy convert(org.genericsystem.api.statics.RemoveStrategy removeStrategy) {
		switch (removeStrategy) {
		case FORCE:
			return RemoveStrategy.FORCE;
		case CONSERVE:
			return RemoveStrategy.CONSERVE;
		default:
			return RemoveStrategy.NORMAL;
		}
	}

}
