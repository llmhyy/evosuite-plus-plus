package org.evosuite.result.seedexpr;

public abstract class PoolSamplingEvent extends SamplingEvent{

	protected int poolSize;
	
	public PoolSamplingEvent(long timestamp, String dataType, int poolSize) {
		super(timestamp, dataType);
		
		this.poolSize = poolSize;
	}
	
}
