package org.evosuite.result.seedexpr;

public class DynamicPoolEvent extends PoolSamplingEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3669953581914648675L;

	public DynamicPoolEvent(long timestamp, String dataType, int poolSize) {
		super(timestamp, dataType, poolSize);
		this.setType(Event.dynamicPoolSampling);
	}

}
