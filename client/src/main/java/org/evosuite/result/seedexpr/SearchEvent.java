package org.evosuite.result.seedexpr;

public class SearchEvent extends SamplingEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6021364375889878588L;

	public SearchEvent(long timestamp, String dataType, String value) {
		super(timestamp, dataType, value);
		this.setType(Event.search);
	}

}
