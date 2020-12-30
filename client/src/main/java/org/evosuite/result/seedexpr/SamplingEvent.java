package org.evosuite.result.seedexpr;

public class SamplingEvent extends Event {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4902325047832256255L;
	private String dataType;
	private String value;
	
	public SamplingEvent(long timestamp, String dataType, String value) {
		super(timestamp);
		this.setDataType(dataType);
		this.setValue(value);
	}
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
