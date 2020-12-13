package org.evosuite.result.seedexpr;

import java.util.ArrayList;
import java.util.List;

public class EventSequence {
	public static List<Event> events = new ArrayList<Event>();
	
	public static void addEvent(Event e) {
		events.add(e);
	}
}
