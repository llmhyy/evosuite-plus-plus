package regression.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute;

public class Trace {
	private static int mTracelevel = 9999;
	private static TraceTarget mTarget = null;

	public static void error(Object message) {
		if (mTracelevel < 1)
			return;
		trace(1, message, null);
	}

	public static void error(Object message, Throwable t) {
		if (mTracelevel < 1)
			return;
		trace(1, message, t);
	}

	public static void warn(Object message) {
		if (mTracelevel < 2)
			return;
		trace(2, message, null);
	}

	private static void trace(int priority, Object message, Throwable t) {
		if (mTracelevel == 9999)
			initialize();
		if (mTracelevel >= priority)
			mTarget.trace(priority, message, t);
	}

	public static void initialize() {
		try {
			Configuration config = Configuration.getInstance();
			Integer traceLevel = new Integer(config.getValue("Base", "Trace", "Level"));
			mTracelevel = traceLevel.intValue();
			String tracerShort = config.getValue("Base", "Trace", "Tracer");
			String tracerName = Configuration.getClass("Trace", tracerShort);
			mTarget = (TraceTarget) Class.forName(tracerName).newInstance();
			System.out.println("Tracing with " + tracerName);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static boolean isInitialized() {
		return (mTracelevel != 9999);
	}
}
