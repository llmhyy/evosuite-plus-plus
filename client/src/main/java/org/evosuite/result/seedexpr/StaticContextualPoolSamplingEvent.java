package org.evosuite.result.seedexpr;

public class StaticContextualPoolSamplingEvent extends PoolSamplingEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4759196688770298441L;

	public StaticContextualPoolSamplingEvent(long timestamp, String dataType, int poolSize, String value) {
		super(timestamp, dataType, poolSize, value);
		this.setType(Event.staticContextPoolSampling);
	}

}
