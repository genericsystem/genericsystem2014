package org.genericsystem.cache;

public class LifeManager {

	private final long designTs;
	private long birthTs;
	private long deathTs;

	public LifeManager(long designTs) {
		this.designTs = designTs;
	}

	public long getBirthTs() {
		return birthTs;
	}

	public void setBirthTs(long birthTs) {
		this.birthTs = birthTs;
	}

	public long getDeathTs() {
		return deathTs;
	}

	public void setDeathTs(long deathTs) {
		this.deathTs = deathTs;
	}

	public long getDesignTs() {
		return designTs;
	}

}
