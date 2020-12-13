package org.evosuite.result.seedexpr;

public abstract class PoolSamplingEvent extends Event{

	protected int poolSize;
	
	public PoolSamplingEvent(long timestamp, String dataType, int poolSize) {
		super(timestamp, dataType);
		
		this.poolSize = poolSize;
	}
	
}
