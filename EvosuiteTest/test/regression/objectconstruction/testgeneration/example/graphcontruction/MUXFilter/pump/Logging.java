package regression.objectconstruction.testgeneration.example.graphcontruction.MUXFilter.pump;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Logging {
	public static final Log processLog = LogFactory.getLog("process");

	public static void logProcess(String origin, String message, LogLevel level, Payload payload) {
		logProcess(origin, message, level, payload, (Throwable) null);
	}

	public static void logProcess(String origin, String message, LogLevel level, Payload payload, Throwable cause) {
		String fullMessage;
		if ((level == LogLevel.WARN && isProcessLogLevel(LogLevel.DEBUG)) || level == LogLevel.TRACE) {
			fullMessage = ((origin == null) ? "" : (origin + ": ")) + message + ". " + payload + ". Content:\n"
					+ getContentSnippet(payload);
		} else {
			fullMessage = ((origin == null) ? "" : (origin + ": ")) + message + ". " + payload;
		}
		if (cause == null) {
			log(fullMessage, processLog, level);
		} else {
			log(fullMessage, processLog, level, cause);
		}
	}

	private static String getContentSnippet(Payload payload) {
		if (payload == null || payload.getRecord() == null)
			return "null";
		String content = payload.getRecord().getContentAsUTF8();
		return (content.length() > 1000) ? content.substring(0, 1000) : content;
	}

	public static boolean isProcessLogLevel(LogLevel level) {
		switch (level) {
		case FATAL:
			return processLog.isFatalEnabled();
		case ERROR:
			return processLog.isErrorEnabled();
		case WARN:
			return processLog.isWarnEnabled();
		case INFO:
			return processLog.isInfoEnabled();
		case DEBUG:
			return processLog.isDebugEnabled();
		case TRACE:
			return processLog.isTraceEnabled();
		}
		return true;
	}

	public static void log(String message, Log log, LogLevel level) {
		switch (level) {
		case FATAL:
			log.fatal(message);
			return;
		case ERROR:
			log.error(message);
			return;
		case WARN:
			log.warn(message);
			return;
		case INFO:
			log.info(message);
			return;
		case DEBUG:
			log.debug(message);
			return;
		case TRACE:
			log.trace(message);
			return;
		}
		log.info("[Unknown log level " + level + "]: " + message);
	}

	public static void log(String message, Log log, LogLevel level, Throwable cause) {
		switch (level) {
		case FATAL:
			log.fatal(message, cause);
			return;
		case ERROR:
			log.error(message, cause);
			return;
		case WARN:
			log.warn(message, cause);
			return;
		case INFO:
			log.info(message, cause);
			return;
		case DEBUG:
			log.debug(message, cause);
			return;
		case TRACE:
			log.trace(message, cause);
			return;
		}
		log.info("[Unknown log level " + level + "]: " + message, cause);
	}

	public enum LogLevel {
		FATAL, ERROR, WARN, INFO, DEBUG, TRACE;
	}
}
