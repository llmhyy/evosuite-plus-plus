package org.evosuite.result.seedexpr;

public class SearchEvent extends Event {

	public SearchEvent(long timestamp, String dataType) {
		super(timestamp, dataType);
		this.setType(Event.search);
	}

}
