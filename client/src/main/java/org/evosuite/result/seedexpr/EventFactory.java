package org.evosuite.result.seedexpr;

public class EventFactory {
	public static PoolSamplingEvent createStaticEvent(boolean isContext, long timestamp, String dataType, int poolSize) {
		if(isContext) {
			return new StaticContextualPoolSamplingEvent(timestamp, dataType, poolSize);
		}
		else {
			return new StaticPoolSamplingEvent(timestamp, dataType, poolSize);
		}
	}
}
