package feature.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute;

import java.util.List;
import java.util.Vector;

public class FileSenderConfiguration {
	private String[] mFilenames = null;
	private String mResolution = null;
	private String mEncoding = null;

	public FileSenderConfiguration(XBUSSystem xbusSystem) throws XException {
		Configuration config = Configuration.getInstance();
		this.mResolution = retrieveResolution(xbusSystem.getName());
		this.mEncoding = retrieveEncoding(xbusSystem.getName());
		String filenameString = config.getValue("System", xbusSystem.getName(), "Filename");
		if (xbusSystem.getBroadcast())
			xbusSystem.getBroadcastData(filenameString);
		this.mFilenames = xbusSystem.replaceAllMarkers(filenameString);
	}

	public String getEncoding() {
		return this.mEncoding;
	}

	public String[] getFileNames() {
		return this.mFilenames;
	}

	public String getResolution() {
		return this.mResolution;
	}

	public String retrieveResolution(String system) throws XException {
		String resolution = Configuration.getInstance().getValue("System", system, "ConflictResolution");
		if (!resolution.equals("Append") && !resolution.equals("Error") && !resolution.equals("Overwrite")
				&& !resolution.equals("Rename")) {
			List<String> params = new Vector();
			params.add(resolution);
			throw new XException("E", "01", "001", "28", params);
		}
		return resolution;
	}

	private String retrieveEncoding(String system) throws XException {
		String configEncoding = Configuration.getInstance().getValueOptional("System", system, "Encoding");
		return (configEncoding == null) ? Constants.SYS_ENCODING : configEncoding;
	}
}
