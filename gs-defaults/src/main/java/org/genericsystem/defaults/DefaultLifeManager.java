package org.genericsystem.defaults;

public interface DefaultLifeManager {

	public final static long TS_OLD_SYSTEM = 1L;
	public final static long TS_SYSTEM = 0L;
	public final static long[] SYSTEM_TS = new long[] { TS_SYSTEM, 0L, Long.MAX_VALUE };
	public final static long[] USER_TS = new long[] { Long.MAX_VALUE, 0L, Long.MAX_VALUE };

	default boolean isSystem() {
		return getBirthTs() == TS_SYSTEM;
	}

	long getBirthTs();
}
