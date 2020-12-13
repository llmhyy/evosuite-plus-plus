package org.evosuite.result.seedexpr;

public class StaticContextualPoolSamplingEvent extends PoolSamplingEvent {

	public StaticContextualPoolSamplingEvent(long timestamp, String dataType, int poolSize) {
		super(timestamp, dataType, poolSize);
		this.setType(Event.staticContextPoolSampling);
	}

}
