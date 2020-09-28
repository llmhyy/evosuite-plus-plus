package regression.objectconstruction.testgeneration.example.graphcontruction.MUXFilter.pump;

import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MUXFilter {
	private static Log log = LogFactory.getLog(MUXFilter.class);
	private List<MUXFilterFeeder> feeders;
	private Payload availablePayload;
	private boolean eofReached;
	private PayloadQueue outqueue;

	public boolean pump() throws IOException {
		if (!hasNext())
			return false;
		Payload next = next();
		Logging.logProcess("MUXFilter", "Calling close for object as part of pump()", Logging.LogLevel.TRACE, next);
		next.close();
		return hasNext();
	}

	public synchronized boolean hasNext() {
		while (true) {
			if (this.availablePayload != null)
				return true;
			if (this.eofReached) {
				log.trace("hasNext() EOF reached");
				return false;
			}
			boolean allSaysEOF = true;
			for (MUXFilterFeeder feeder : this.feeders) {
				if (!feeder.isEOFReached()) {
					allSaysEOF = false;
					break;
				}
			}
			this.availablePayload = drain();
			if (this.availablePayload != null)
				return true;
			if (allSaysEOF) {
				log.trace("hasNext() allSaysEOF and availablePayload == null");
				this.eofReached = true;
				return false;
			}
			Payload next = this.outqueue.uninterruptibleTake();
			if (next != MUXFilterFeeder.STOP)
				this.availablePayload = next;
		}
	}

	public Payload next() {
		log.trace("Next() called");
		if (!hasNext())
			throw new IllegalStateException("No more elements");
		Payload returnPayload = this.availablePayload;
		this.availablePayload = null;
		return returnPayload;
	}

	private Payload drain() {
		Payload next = null;
		while (this.outqueue.size() > 0) {
			next = this.outqueue.uninterruptibleTake();
			if (next != MUXFilterFeeder.STOP)
				break;
		}
		return (next == MUXFilterFeeder.STOP) ? null : next;
	}
}
