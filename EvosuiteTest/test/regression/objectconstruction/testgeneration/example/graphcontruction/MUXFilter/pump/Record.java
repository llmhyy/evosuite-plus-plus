package regression.objectconstruction.testgeneration.example.graphcontruction.MUXFilter.pump;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Record implements Serializable, Comparable {
	public static final long serialVersionUID = 35848318185L;

	private static Log log = LogFactory.getLog(Record.class);

	private static final byte[] EMPTY_CONTENT = new byte[0];

	public static final String META_VALIDATION_STATE = "ValidationState";

	public static final String ID_DELIMITER = ";";

	private String id;

	private String base;

	private boolean deleted = false;

	private boolean indexable = true;

	private byte[] data;

	private long creationTime = System.currentTimeMillis();

	private long modificationTime = this.creationTime;

	private LinkedHashSet<String> parentIds;

	private LinkedHashSet<Record> parents;

	private LinkedHashSet<String> childIds;

	private LinkedHashSet<Record> children;

	private StringMap meta;

	private boolean contentCompressed;

	public Record(String id, String base, byte[] data) {
		long now = System.currentTimeMillis();
		init(id, base, false, true, data, now, now, null, null, null, false);
	}

	public void init(String id, String base, boolean deleted, boolean indexable, byte[] data, long creationTime,
			long lastModified, List<String> parents, List<String> children, StringMap meta, boolean contentCompressed) {
		setId(id);
		setBase(base);
		setDeleted(deleted);
		setIndexable(indexable);
		setRawContent(data);
		setCreationTime(creationTime);
		setModificationTime(lastModified);
		setParentIds(parents);
		setChildIds(children);
		setChildren(null);
		this.contentCompressed = contentCompressed;
		this.meta = meta;
		if (log.isTraceEnabled()) {
			log.trace("Created " + toString(true));
		} else if (log.isDebugEnabled()) {
			log.trace("Created " + toString());
		}
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		if (id == null)
			throw new IllegalArgumentException("ID must be specified");
		if ("".equals(id))
			throw new IllegalArgumentException("ID must not be the empty string");
		this.id = id;
	}

	public String getBase() {
		return this.base;
	}

	public void setBase(String base) {
		if (base == null)
			throw new IllegalArgumentException("base must be specified for record '" + getId() + "'");
		if ("".equals(base))
			throw new IllegalArgumentException("base must not be the mpty string for record '" + getId() + "'");
		this.base = base;
	}

	public boolean isDeleted() {
		return this.deleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.deleted = isDeleted;
	}

	public boolean isIndexable() {
		return this.indexable;
	}

	public void setIndexable(boolean isIndexable) {
		this.indexable = isIndexable;
	}

	public void setRawContent(byte[] content) {
		setRawContent(content, false);
	}

	public void setRawContent(byte[] content, boolean contentCompressed) {
		if (content == null) {
			content = EMPTY_CONTENT;
			log.trace("setRawContent(null, ...) was called. Assigning byte-array of length 0 as content");
		}
		this.data = content;
		this.contentCompressed = contentCompressed;
	}

	public long getCreationTime() {
		return this.creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getModificationTime() {
		return this.modificationTime;
	}

	public void setModificationTime(long modificationTime) {
		this.modificationTime = modificationTime;
	}

	public long getLength() {
		return (this.data == null) ? 0L : this.data.length;
	}

	public void setParentIds(List<String> parentIds) {
		if (parentIds == null) {
			this.parentIds = null;
		} else if (parentIds.isEmpty()) {
			log.warn(
					"The non-existence of a parentId should be stated by null, not the empty list. Problematic Record with id '"
							+ getId() + "' from base '" + getBase() + "'. Continuing creation");
			this.parentIds = null;
		} else {
			this.parentIds = new LinkedHashSet<String>(parentIds);
		}
		this.parents = null;
	}

	public void setChildIds(List<String> childIds) {
		if (childIds == null) {
			this.childIds = null;
			return;
		}
		if (childIds.isEmpty()) {
			log.warn("No childIds should be stated by null, not the empty list. Problematic Record with id '" + getId()
					+ "' from base '" + getBase() + "'. Continuing creation");
			this.childIds = null;
		} else {
			this.childIds = new LinkedHashSet<String>(childIds);
		}
		this.children = null;
	}

	public void setChildren(List<Record> children) {
		if (children == null) {
			this.children = null;
			return;
		}
		List<String> newChildIds = new ArrayList<String>(children.size());
		for (Record child : children)
			newChildIds.add(child.getId());
		setChildIds(newChildIds);
		this.children = new LinkedHashSet<Record>(children);
	}

	public int compareTo(Object o) {
		return getId().compareTo(((Record) o).getId());
	}

	public String toString() {
		return toString(false);
	}

	public String toString(boolean verbose) {
		return "Record [id(" + getId() + "), base(" + getBase() + "), deleted(" + isDeleted() + "), indexable("
				+ isIndexable() + "), data-length(" + getLength() + "), num-childrenIDs("
				+ ((this.childIds == null) ? 0 : this.childIds.size()) + "), num-parentsIDs("
				+ ((this.parentIds == null) ? 0 : this.parentIds.size()) + "), num-childRecords("
				+ ((this.children == null) ? 0 : this.children.size()) + "), num-parentRecords("
				+ ((this.parents == null) ? 0 : this.parents.size()) + ")"
				+ (verbose
						? (", creationTime(" + timeToString(getCreationTime()) + "), modificationTime("
								+ timeToString(getModificationTime()) + "), parentIds("
								+ ((this.parentIds == null) ? ""
										: Logs.expand(new ArrayList<String>(this.parentIds), 5))
								+ "), childIds("
								+ ((this.childIds == null) ? "" : Logs.expand(new ArrayList<String>(this.childIds), 5))
								+ "), meta("
								+ ((this.meta == null) ? ""
										: Logs.expand(Arrays.asList(this.meta.keySet().toArray()), 5)))
						: "")
				+ ")]";
	}

	private String timeToString(long timestamp) {
		if (timestamp == 0L)
			return "NA";
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		return String.format("%1$tF %1$tT", new Object[] { calendar });
	}
	
	public String getContentAsUTF8() {
		try {
			return new String(getContent(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Could not convert uning utf-8");
		}
	}

	public byte[] getContent() {
		return getContent(true);
	}

	public byte[] getContent(boolean autoUncompress) {
		if (this.contentCompressed && autoUncompress)
			return Zips.gunzipBuffer(this.data);
		return this.data;
	}
}
