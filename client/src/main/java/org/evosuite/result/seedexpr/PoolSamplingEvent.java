package org.evosuite.result.seedexpr;

public abstract class PoolSamplingEvent extends SamplingEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4608673864697927667L;
	protected int poolSize;
	
	public PoolSamplingEvent(long timestamp, String dataType, int poolSize) {
		super(timestamp, dataType);
		
		this.poolSize = poolSize;
	}
	
}
