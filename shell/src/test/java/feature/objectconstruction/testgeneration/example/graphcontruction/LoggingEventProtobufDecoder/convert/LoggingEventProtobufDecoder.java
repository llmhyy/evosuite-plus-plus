package feature.objectconstruction.testgeneration.example.graphcontruction.LoggingEventProtobufDecoder.convert;

public class LoggingEventProtobufDecoder {
//	public static LoggingEvent convert(LoggingProto.LoggingEvent parsedEvent) {
//		if (parsedEvent == null)
//			return null;
//		LoggingEvent result = new LoggingEvent();
//		if (parsedEvent.hasLoggerName())
//			result.setLogger(parsedEvent.getLoggerName());
//		if (parsedEvent.hasSequenceNumber())
//			result.setSequenceNumber(Long.valueOf(parsedEvent.getSequenceNumber()));
//		if (parsedEvent.hasThreadInfo())
//			result.setThreadInfo(convert(parsedEvent.getThreadInfo()));
//		if (parsedEvent.hasLevel()) {
//			LoggingProto.Level level = parsedEvent.getLevel();
//			switch (level) {
//			case TRACE:
//				result.setLevel(LoggingEvent.Level.TRACE);
//				break;
//			case DEBUG:
//				result.setLevel(LoggingEvent.Level.DEBUG);
//				break;
//			case INFO:
//				result.setLevel(LoggingEvent.Level.INFO);
//				break;
//			case WARN:
//				result.setLevel(LoggingEvent.Level.WARN);
//				break;
//			case ERROR:
//				result.setLevel(LoggingEvent.Level.ERROR);
//				break;
//			}
//		}
//		if (parsedEvent.hasLoggerContext())
//			result.setLoggerContext(convert(parsedEvent.getLoggerContext()));
//		if (parsedEvent.hasThrowable())
//			result.setThrowable(convert(parsedEvent.getThrowable()));
//		if (parsedEvent.hasMarker())
//			result.setMarker(convert(parsedEvent.getMarker()));
//		int count = parsedEvent.getCallStackElementCount();
//		if (count > 0) {
//			List<LoggingProto.StackTraceElement> callStackElements = parsedEvent.getCallStackElementList();
//			ExtendedStackTraceElement[] callStack = new ExtendedStackTraceElement[count];
//			for (int i = 0; i < count; i++) {
//				LoggingProto.StackTraceElement current = callStackElements.get(i);
//				callStack[i] = convert(current);
//			}
//			result.setCallStack(callStack);
//		}
//		if (parsedEvent.hasTimeStamp())
//			result.setTimeStamp(Long.valueOf(parsedEvent.getTimeStamp()));
//		if (parsedEvent.hasMessage())
//			result.setMessage(convert(parsedEvent.getMessage()));
//		if (parsedEvent.hasMappedDiagnosticContext())
//			result.setMdc(convert(parsedEvent.getMappedDiagnosticContext()));
//		if (parsedEvent.hasNestedDiagnosticContext()) {
//			LoggingProto.NestedDiagnosticContext parsedNdc = parsedEvent.getNestedDiagnosticContext();
//			int entryCount = parsedNdc.getEntryCount();
//			if (entryCount > 0) {
//				List<LoggingProto.Message> entryList = parsedNdc.getEntryList();
//				Message[] ndc = new Message[entryCount];
//				for (int i = 0; i < entryCount; i++)
//					ndc[i] = convert(entryList.get(i));
//				result.setNdc(ndc);
//			}
//		}
//		return result;
//	}
//
//	public static ThreadInfo convert(LoggingProto.ThreadInfo parsedThreadInfo) {
//		if (parsedThreadInfo == null)
//			return null;
//		Long threadId = null;
//		if (parsedThreadInfo.hasId())
//			threadId = Long.valueOf(parsedThreadInfo.getId());
//		String threadName = null;
//		if (parsedThreadInfo.hasName())
//			threadName = parsedThreadInfo.getName();
//		Long threadGroupId = null;
//		if (parsedThreadInfo.hasGroupId())
//			threadGroupId = Long.valueOf(parsedThreadInfo.getGroupId());
//		String threadGroupName = null;
//		if (parsedThreadInfo.hasGroupName())
//			threadGroupName = parsedThreadInfo.getGroupName();
//		return new ThreadInfo(threadId, threadName, threadGroupId, threadGroupName);
//	}
}
