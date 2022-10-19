package testcode.graphgeneration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Relation {
	FIELD, ARRAY_ELEMENT, PARAM;

	private static final List<Relation> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
	private static final int SIZE = VALUES.size();

	public static Relation randomRelation() {
		return VALUES.get(RandomNumberGenerator.getInstance().nextInt(SIZE-2));
	}
}
