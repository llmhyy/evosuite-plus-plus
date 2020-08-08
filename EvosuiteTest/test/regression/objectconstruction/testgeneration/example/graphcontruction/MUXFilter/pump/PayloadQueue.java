package regression.objectconstruction.testgeneration.example.graphcontruction.MUXFilter.pump;

import java.util.concurrent.ArrayBlockingQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PayloadQueue extends ArrayBlockingQueue<Payload> {
	private static Log log = LogFactory.getLog(PayloadQueue.class);
	private long maxSize;
	private final Object flag = new Object();

	public PayloadQueue(int maxCount, long maxSize) {
		super(maxCount, true);
		this.maxSize = maxSize;
		log.debug("Constructed PayloadQueue with max Payloads " + maxCount + " and max bytes " + maxSize);
	}

	public Payload uninterruptibleTake() {
		while (true) {
			try {
				Payload result = take();
				synchronized (this.flag) {
					this.flag.notifyAll();
					return result;
				}
			} catch (InterruptedException e) {
				log.warn("Got InterruptedException while taking in uninterruptibleTake. Retrying", e);
			}
		}
	}
}
