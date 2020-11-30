package feature.objectconstruction.testgeneration.example.graphcontruction.MUXFilter.pump;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;

public class Logs {
	private static final int DEFAULT_MAXLENGTH = 3;

	private static final int VERBOSE_MAXLENGTH = 10;

	private static final int DEFAULT_MAXDEPTH = 2;

	private static final int VERBOSE_MAXDEPTH = 4;

	public enum Level {
		TRACE, DEBUG, INFO, WARN, ERROR, FATAL;
	}

	public static void log(Log log, Level level, String message, Throwable error, boolean verbose, Object... elements) {
		int maxLength = verbose ? 10 : 3;
		int maxDepth = verbose ? 4 : 2;
		String expanded = message;
		if (elements != null && elements.length > 0)
			expanded = expanded + expand(elements, maxLength, maxDepth);
		switch (level) {
		case TRACE:
			if (!log.isTraceEnabled())
				return;
			if (error == null) {
				log.trace(expanded);
			} else {
				log.trace(expanded, error);
			}
			return;
		case DEBUG:
			if (!log.isDebugEnabled())
				return;
			if (error == null) {
				log.debug(expanded);
			} else {
				log.debug(expanded, error);
			}
			return;
		case INFO:
			if (!log.isInfoEnabled())
				return;
			if (error == null) {
				log.info(expanded);
			} else {
				log.info(expanded, error);
			}
			return;
		case WARN:
			if (!log.isWarnEnabled())
				return;
			if (error == null) {
				log.warn(expanded);
			} else {
				log.warn(expanded, error);
			}
			return;
		case ERROR:
			if (!log.isErrorEnabled())
				return;
			if (error == null) {
				log.error(expanded);
			} else {
				log.error(expanded, error);
			}
			return;
		case FATAL:
			if (!log.isFatalEnabled())
				return;
			if (error == null) {
				log.fatal(expanded);
			} else {
				log.fatal(expanded, error);
			}
			return;
		}
		throw new IllegalArgumentException("The level '" + level + "' is unknown");
	}

	public static void log(Log log, Level level, String message, Throwable t) {
		log(log, level, message, t, false, new Object[0]);
	}

	public static void log(Log log, Level level, String message, Object... elements) {
		log(log, level, message, null, false, elements);
	}

	protected static String expand(Object... elements) {
		if (elements == null)
			return "";
		if (elements.length == 0)
			return "";
		return expand(elements, false);
	}

	protected static String expand(Object element) {
		return expand(element, false);
	}

	protected static String expand(Object element, boolean verbose) {
		int maxLength = verbose ? 10 : 3;
		int maxDepth = verbose ? 4 : 2;
		return expand(element, maxLength, maxDepth);
	}

	protected static String expand(Object element, int maxLength, int maxDepth) {
		StringWriter writer = new StringWriter(200);
		expand(writer, element, maxLength, maxDepth);
		return writer.toString();
	}

	protected static void expand(StringWriter writer, Object element, int maxLength, int maxDepth) {
		if (element instanceof Set) {
			expand(writer, (Set) element, maxLength, maxDepth);
		} else if (element instanceof Map) {
			expand(writer, (Map) element, maxLength, maxDepth);
		} else if (element instanceof List) {
			expand(writer, (List) element, maxLength, maxDepth, ((List) element).size());
		} else if (element instanceof Object[]) {
			Object[] array = (Object[]) element;
			int wanted = Math.min(array.length, maxLength + 1);
			List<Object> list = new ArrayList(wanted);
			int counter = 0;
			for (Object value : array) {
				if (counter++ == wanted)
					break;
				list.add(value);
			}
			expand(writer, list, maxLength, maxDepth, array.length);
		} else if (element instanceof byte[]) {
			byte[] array = (byte[]) element;
			int wanted = Math.min(array.length, maxLength + 1);
			List<Byte> list = new ArrayList<Byte>(wanted);
			int counter = 0;
			for (byte value : array) {
				if (counter++ == wanted)
					break;
				list.add(Byte.valueOf(value));
			}
			expand(writer, list, maxLength, maxDepth, array.length);
		} else if (element instanceof short[]) {
			short[] array = (short[]) element;
			int wanted = Math.min(array.length, maxLength + 1);
			List<Short> list = new ArrayList<Short>(wanted);
			int counter = 0;
			for (short value : array) {
				if (counter++ == wanted)
					break;
				list.add(Short.valueOf(value));
			}
			expand(writer, list, maxLength, maxDepth, array.length);
		} else if (element instanceof int[]) {
			int[] array = (int[]) element;
			int wanted = Math.min(array.length, maxLength + 1);
			List<Integer> list = new ArrayList<Integer>(wanted);
			int counter = 0;
			for (int value : array) {
				if (counter++ == wanted)
					break;
				list.add(Integer.valueOf(value));
			}
			expand(writer, list, maxLength, maxDepth, array.length);
		} else if (element instanceof long[]) {
			long[] array = (long[]) element;
			int wanted = Math.min(array.length, maxLength + 1);
			List<Long> list = new ArrayList<Long>(wanted);
			int counter = 0;
			for (long value : array) {
				if (counter++ == wanted)
					break;
				list.add(Long.valueOf(value));
			}
			expand(writer, list, maxLength, maxDepth, array.length);
		} else if (element instanceof float[]) {
			float[] array = (float[]) element;
			int wanted = Math.min(array.length, maxLength + 1);
			List<Float> list = new ArrayList<Float>(wanted);
			int counter = 0;
			for (float value : array) {
				if (counter++ == wanted)
					break;
				list.add(Float.valueOf(value));
			}
			expand(writer, list, maxLength, maxDepth, array.length);
		} else if (element instanceof double[]) {
			double[] array = (double[]) element;
			int wanted = Math.min(array.length, maxLength + 1);
			List<Double> list = new ArrayList<Double>(wanted);
			int counter = 0;
			for (double value : array) {
				if (counter++ == wanted)
					break;
				list.add(Double.valueOf(value));
			}
			expand(writer, list, maxLength, maxDepth, array.length);
		} else if (element instanceof boolean[]) {
			boolean[] array = (boolean[]) element;
			int wanted = Math.min(array.length, maxLength + 1);
			List<Boolean> list = new ArrayList<Boolean>(wanted);
			int counter = 0;
			for (boolean value : array) {
				if (counter++ == wanted)
					break;
				list.add(Boolean.valueOf(value));
			}
			expand(writer, list, maxLength, maxDepth, array.length);
		} else if (element instanceof char[]) {
			char[] array = (char[]) element;
			int wanted = Math.min(array.length, maxLength + 1);
			List<Character> list = new ArrayList<Character>(wanted);
			int counter = 0;
			for (char value : array) {
				if (counter++ == wanted)
					break;
				list.add(Character.valueOf(value));
			}
			expand(writer, list, maxLength, maxDepth, array.length);
		} else {
			writer.append(element.toString());
		}
	}

	public static String expand(List list, int maxLength) {
		StringWriter sw = new StringWriter(Math.min(maxLength, list.size()) * 20);
		expand(sw, list, maxLength, 1, list.size());
		return sw.toString();
	}

	protected static void expand(StringWriter writer, List list, int maxLength, int maxDepth, int listLength) {
		writer.append(Integer.toString(listLength));
		if (maxDepth == 0) {
			writer.append("(...)");
			return;
		}
		int num = (listLength <= maxLength + 1) ? list.size() : Math.max(1, maxLength);
		writer.append("(");
		int counter = 0;
		for (Object object : list) {
			expand(writer, object, maxLength, maxDepth - 1);
			if (counter++ < num - 1)
				writer.append(", ");
		}
		if (num < listLength)
			writer.append(", ...");
		writer.append(")");
	}

	protected static void expand(StringWriter writer, Set set, int maxLength, int maxDepth) {
		writer.append(Integer.toString(set.size()));
		if (maxDepth == 0) {
			writer.append("(...)");
			return;
		}
		int num = (set.size() <= maxLength + 1) ? set.size() : Math.max(1, maxLength);
		writer.append("(");
		int counter = 0;
		for (Object object : set) {
			expand(writer, object, maxLength, maxDepth - 1);
			if (counter++ < num - 1)
				writer.append(", ");
		}
		if (num < set.size())
			writer.append(", ...");
		writer.append(")");
	}

	protected static void expand(StringWriter writer, Map map, int maxLength, int maxDepth) {
		writer.append(Integer.toString(map.size()));
		if (maxDepth == 0) {
			writer.append("(...)");
			return;
		}
		int num = (map.size() <= maxLength + 1) ? map.size() : Math.max(1, maxLength);
		writer.append("(");
		int counter = 0;
		for (Object oEntry : map.entrySet()) {
			Map.Entry entry = (Map.Entry) oEntry;
			writer.append("{");
			expand(writer, entry.getKey(), maxLength, maxDepth - 1);
			writer.append(", ");
			expand(writer, entry.getValue(), maxLength, maxDepth - 1);
			writer.append("}");
			if (counter++ < num - 1)
				writer.append(", ");
		}
		if (num < map.size())
			writer.append(", ...");
		writer.append(")");
	}
}
