package org.evosuite.result.seedexpr;

public class StaticContextualPoolSamplingEvent extends PoolSamplingEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4759196688770298441L;

	public StaticContextualPoolSamplingEvent(long timestamp, String dataType, int poolSize) {
		super(timestamp, dataType, poolSize);
		this.setType(Event.staticContextPoolSampling);
	}

}
