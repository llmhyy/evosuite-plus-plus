package org.evosuite.result.seedexpr;

public class RandomSamplingEvent extends Event {
	

	public RandomSamplingEvent(long timestamp, String dataType) {
		super(timestamp, dataType);
		this.setType(Event.randomSampling);
	}

	
}
