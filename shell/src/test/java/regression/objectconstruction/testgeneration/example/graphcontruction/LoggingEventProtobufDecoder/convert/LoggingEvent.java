package regression.objectconstruction.testgeneration.example.graphcontruction.LoggingEventProtobufDecoder.convert;

public class LoggingEvent {
	private String logger;
	private Long sequenceNumber;
	private ThreadInfo threadInfo;

	public void setLogger(String logger) {
		this.logger = logger;
	}

	public void setSequenceNumber(Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public void setThreadInfo(ThreadInfo threadInfo) {
		this.threadInfo = threadInfo;
	}
}
