package feature.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Constants {
	public static final String FILE_SEPERATOR = File.separator;

	public static final String XBUS_HOME = System.getProperty("xbus.home");

	public static final String XBUS_ETC = String.valueOf(XBUS_HOME) + FILE_SEPERATOR + "etc" + FILE_SEPERATOR;

	public static final String XBUS_PLUGIN_ETC = String.valueOf(XBUS_HOME) + FILE_SEPERATOR + "plugin" + FILE_SEPERATOR
			+ "etc" + FILE_SEPERATOR;

	public static final String LINE_SEPERATOR = System.getProperty("line.separator");

	public static final String SYS_ENCODING = System.getProperty("file.encoding");

	public static final String getDateAsString() {
		return (new SimpleDateFormat(".yyyyMMddHHmmssSSS")).format(new Date());
	}
}
