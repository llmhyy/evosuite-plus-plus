package org.evosuite.result.seedexpr;

public class StaticPoolSamplingEvent extends PoolSamplingEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4207562665252964436L;

	public StaticPoolSamplingEvent(long timestamp, String dataType, int poolSize, String value) {
		super(timestamp, dataType, poolSize, value);
		this.setType(Event.staticPoolSampling);
	}

}
