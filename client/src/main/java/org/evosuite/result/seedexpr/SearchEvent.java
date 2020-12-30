package org.evosuite.result.seedexpr;

public class SearchEvent extends SamplingEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6021364375889878588L;

	private String oldValue;
	
	public SearchEvent(long timestamp, String dataType, String value, String oldValue) {
		super(timestamp, dataType, value);
		this.setType(Event.search);
		this.setOldValue(oldValue);
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

}
