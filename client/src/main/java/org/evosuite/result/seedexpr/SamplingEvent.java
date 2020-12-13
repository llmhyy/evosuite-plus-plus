package org.evosuite.result.seedexpr;

public class SamplingEvent extends Event {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4902325047832256255L;
	private String dataType;
	
	public SamplingEvent(long timestamp, String dataType) {
		super(timestamp);
		this.setDataType(dataType);
	}
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
