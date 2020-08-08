package regression.objectconstruction.testgeneration.example.graphcontruction.MUXFilter.pump;

public class MUXFilterFeeder {
	public static final Payload STOP = new Payload(new Record("EOF", "Dummy", new byte[0]));
	private boolean eofReached = false;

	public boolean isEOFReached() {
		return this.eofReached;
	}
}
