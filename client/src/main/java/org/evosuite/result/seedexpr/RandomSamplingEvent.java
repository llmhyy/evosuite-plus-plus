package org.evosuite.result.seedexpr;

public class RandomSamplingEvent extends SamplingEvent {
	

	public RandomSamplingEvent(long timestamp, String dataType) {
		super(timestamp, dataType);
		this.setType(Event.randomSampling);
	}

	
}
