package org.evosuite.result.seedexpr;

public class RandomSamplingEvent extends SamplingEvent {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 8904637161593616280L;

	public RandomSamplingEvent(long timestamp, String dataType, String value) {
		super(timestamp, dataType, value);
		this.setType(Event.randomSampling);
	}

	
}
