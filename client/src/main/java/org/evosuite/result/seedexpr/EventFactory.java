package org.evosuite.result.seedexpr;

public class EventFactory {
	public static PoolSamplingEvent createStaticEvent(boolean isContext, long timestamp, String dataType, int poolSize, String value) {
		if(isContext) {
			return new StaticContextualPoolSamplingEvent(timestamp, dataType, poolSize, value);
		}
		else {
			return new StaticPoolSamplingEvent(timestamp, dataType, poolSize, value);
		}
	}
}
