package org.evosuite.result.seedexpr;

import java.io.Serializable;

public abstract class Event implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6069894109502320030L;
	
	public static final int branchCovering = 0;
	public static final int staticPoolSampling = 1;
	public static final int staticContextPoolSampling = 2;
	public static final int dynamicPoolSampling = 3;
	public static final int randomSampling = 4;
	public static final int search = 5;
	
	private long timestamp;
	private int type;
	
	
	public Event(long timestamp) {
		this.setTimestamp(timestamp);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String toString() {
		return "Event: " + getStringValue(this.type);
	}

	private String getStringValue(int eventType) {
		switch(eventType) {
		case branchCovering:
			return "branchCovering";
		case staticPoolSampling:
			return "staticPoolSampling";
		case staticContextPoolSampling:
			return "staticContextPoolSampling";
		case dynamicPoolSampling:
			return "dynamicPoolSampling";
		case randomSampling:
			return "randomSampling";
		case search:
			return "search";
		}
		
		return "unknown";
	}
}
