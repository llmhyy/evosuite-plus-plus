package org.evosuite.result.seedexpr;

public class DynamicPoolEvent extends PoolSamplingEvent {

	public DynamicPoolEvent(long timestamp, String dataType, int poolSize) {
		super(timestamp, dataType, poolSize);
		this.setType(Event.dynamicPoolSampling);
	}

}
