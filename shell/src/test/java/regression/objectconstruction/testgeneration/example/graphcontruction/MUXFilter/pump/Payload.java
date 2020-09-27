package regression.objectconstruction.testgeneration.example.graphcontruction.MUXFilter.pump;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Payload {
	private static Log log = LogFactory.getLog(Payload.class);

	public static final String LUCENE_DOCUMENT = "luceneDocument";

	public static final String SEARCH_DESCRIPTOR = "searchDescriptor";

	private InputStream stream = null;

	private Record record = null;

	private boolean closed = false;

	private ConvenientMap data;

	public static final int EOF = -1;

	public static final String ORIGIN = "filename";

	public Payload(InputStream stream) {
		assignIfValid(stream, this.record);
		Logging.logProcess(getClass().getSimpleName(), "Created based on InputStream", Logging.LogLevel.DEBUG, this);
	}

	public Payload(Record record) {
		assignIfValid(this.stream, record);
		Logging.logProcess(getClass().getSimpleName(), "Created based on Record", Logging.LogLevel.DEBUG, this);
	}

	public Payload(InputStream stream, Record record) {
		assignIfValid(stream, record);
		Logging.logProcess(getClass().getSimpleName(), "Created based on Record and InputStream",
				Logging.LogLevel.DEBUG, this);
	}

	public InputStream getStream() {
		return this.stream;
	}

	public Record getRecord() {
		return this.record;
	}

	public ConvenientMap getData() {
		if (this.data == null)
			this.data = new ConvenientMap();
		return this.data;
	}

	public Object getData(String key) {
		try {
			return (this.data == null) ? null : this.data.get(key);
		} catch (NullPointerException e) {
			return null;
		}
	}

	public String getStringData(String key) {
		Object object = getData(key);
		if (object == null || !(object instanceof String))
			return null;
		return (String) object;
	}

	public boolean hasData() {
		return (this.data != null);
	}

	public String getId() {
		try {
			String id = getStringData("recordID");
			if (id != null)
				return id;
			if (getRecord() != null)
				return getRecord().getId();
		} catch (Exception e) {
			log.error("Exception extracting ID from payload '" + super.toString() + "'. Returning null", e);
			return null;
		}
		log.trace("Could not extract ID for payload '" + super.toString() + "'");
		return null;
	}

	public void setID(String id) {
		log.trace("setID(" + id + ") called");
		if (id == null || "".equals(id))
			throw new IllegalArgumentException("The id must be defined");
		getData().put("recordID", id);
		if (getRecord() != null)
			getRecord().setId(id);
	}

	public void setStream(InputStream stream) {
		assignIfValid(stream, this.record);
	}

	public void setRecord(Record record) {
		assignIfValid(this.stream, record);
	}

	private void assignIfValid(InputStream stream, Record record) {
		if (stream == null && record == null)
			throw new IllegalStateException("Either stream or record must be defined");
		log.trace("Assigned stream: " + stream + " and record: " + record + " to Payload");
		this.stream = stream;
		this.record = record;
	}

	public void close() {
		if (this.closed) {
			log.trace("close(): Already closed. Ignoring request");
			return;
		}
		this.closed = true;
		Logging.logProcess(getClass().getSimpleName(), "Closing payload", Logging.LogLevel.TRACE, this);
		if (this.stream != null)
			try {
				log.trace("Closing embedded stream for " + this);
				this.stream.close();
			} catch (IOException e) {
				log.error("Exception closing embedded stream for " + this, e);
			}
	}

	public boolean pump() throws IOException {
		boolean cont = (this.stream != null && this.stream.read() != -1);
		if (!cont) {
			Logging.logProcess(getClass().getSimpleName(), "Calling close due to pump() being finished",
					Logging.LogLevel.TRACE, this);
			close();
		}
		return cont;
	}

	public Payload clone() {
		Payload clone = new Payload(getStream(), getRecord());
		if (this.data != null)
			clone.getData().putAll((Map) this.data);
		return clone;
	}

	public String toString() {
		return "Payload(" + getId() + ")"
				+ ((getData("filename") == null) ? "" : (" with origin '" + getData("filename") + "'"))
				+ (hasData() ? (" with " + getData().size() + " meta data") : "");
	}

	public CharSequence toString(boolean verbose) {
		if (!verbose)
			return toString();
		StringBuffer sb = new StringBuffer(100);
		sb.append("Payload(").append(getId()).append(")");
		sb.append((getData("filename") == null) ? "" : (" with origin '" + getData("filename")));
		sb.append(". MetaData:");
		if (this.data == null) {
			sb.append(" none");
		} else {
			for (Map.Entry entry : getData().entrySet()) {
				sb.append(" ");
				sb.append(entry.getKey().toString()).append(":");
				if (entry.getValue() instanceof String) {
					if (((String) entry.getValue()).length() > 40) {
						sb.append(((String) entry.getValue()).substring(0, 20));
						sb.append("... (");
						sb.append(((String) entry.getValue()).length());
						sb.append(" characters)");
					}
					sb.append(entry.getValue().toString());
				}
			}
		}
		return sb.toString();
	}
}
