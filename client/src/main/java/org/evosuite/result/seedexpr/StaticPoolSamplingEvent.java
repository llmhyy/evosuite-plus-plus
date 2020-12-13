package org.evosuite.result.seedexpr;

public class StaticPoolSamplingEvent extends PoolSamplingEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4207562665252964436L;

	public StaticPoolSamplingEvent(long timestamp, String dataType, int poolSize) {
		super(timestamp, dataType, poolSize);
		this.setType(Event.staticPoolSampling);
	}

}
