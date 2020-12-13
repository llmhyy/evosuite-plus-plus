package org.evosuite.result.seedexpr;

public class StaticPoolSamplingEvent extends PoolSamplingEvent {
	
	public StaticPoolSamplingEvent(long timestamp, String dataType, int poolSize) {
		super(timestamp, dataType, poolSize);
		this.setType(Event.staticPoolSampling);
	}

}
