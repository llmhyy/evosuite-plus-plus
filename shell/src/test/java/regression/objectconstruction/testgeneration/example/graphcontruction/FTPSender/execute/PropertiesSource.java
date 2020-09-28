package regression.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public class PropertiesSource implements ConfigSource {
	private String mPrefix = null;

	public PropertiesSource(String source) {
		if (source == null) {
			System.out.println("I_00_001_2 Source must not be <null>");
			System.exit(1);
		}
		this.mPrefix = source;
	}

	public Hashtable readCache() {
		Hashtable<String, Hashtable<Object, Object>> cache = new Hashtable<String, Hashtable<Object, Object>>();
		Properties props = new Properties();
		addProperties(props, this.mPrefix, Constants.XBUS_ETC);
		addProperties(props, this.mPrefix, Constants.XBUS_PLUGIN_ETC);
		for (Enumeration<?> e = props.propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = props.getProperty(key);
			Vector cacheKeys = splitKey(key);
			cache = putCache(cache, cacheKeys, value);
		}
		return cache;
	}

	private void addProperties(Properties props, String prefix, String directory) {
		File etcDir = new File(directory);
		String[] configFiles = etcDir.list(new PropertiesFilter());
		if (Constants.XBUS_ETC.equals(directory) && "standard".equals(this.mPrefix)
				&& (configFiles == null || configFiles.length == 0)) {
			System.out.println("I_00_001_2 No configuration file " + prefix + "*.conf"
					+ " exists, maybe XBUS_HOME is not set properly");
			System.exit(1);
		}
		for (int i = 0; configFiles != null && i < configFiles.length; i++) {
			Properties newProps = new Properties();
			try {
				FileInputStream instream = new FileInputStream(String.valueOf(directory) + configFiles[i]);
				newProps.load(instream);
				instream.close();
			} catch (FileNotFoundException e) {
				System.out.println("I_00_001_2 File " + directory + configFiles[i] + " doesn't exist");
				System.exit(1);
			} catch (IOException e) {
				System.out.println("I_00_001_2 IOException while reading file  " + directory + configFiles[i]);
				e.printStackTrace();
				System.exit(1);
			}
			String key = null;
			for (Enumeration<Object> keys = newProps.keys(); keys.hasMoreElements();) {
				key = (String) keys.nextElement();
				if (props.containsKey(key)) {
					System.out.println("I_00_001_5 Key " + key + " has already been inserted");
					System.exit(1);
				}
				props.put(key, newProps.get(key));
			}
		}
	}

	private Vector splitKey(String key) {
		StringTokenizer st = new StringTokenizer(key, "_");
		Vector<String> tmp = new Vector();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			tmp.add(token);
		}
		if (tmp.size() != 3) {
			System.out.println("I_00_001_4 Wrong format of key " + key);
			System.exit(1);
		}
		return tmp;
	}

	private Hashtable putCache(Hashtable<String, Hashtable<Object, Object>> cache, Vector<String> cacheKeys,
			String value) {
		Hashtable<Object, Object> sectionTable = null;
		boolean newChapter = false;
		Hashtable<Object, Object> keyTable = null;
		boolean newSection = false;
		String chapter = cacheKeys.elementAt(0);
		String section = cacheKeys.elementAt(1);
		String cacheKey = cacheKeys.elementAt(2);
		sectionTable = (Hashtable) cache.get(chapter);
		if (sectionTable == null) {
			sectionTable = new Hashtable<Object, Object>();
			newChapter = true;
		}
		keyTable = (Hashtable) sectionTable.get(section);
		if (keyTable == null) {
			keyTable = new Hashtable<Object, Object>();
			newSection = true;
		}
		keyTable.put(cacheKey, value.trim());
		if (newSection)
			sectionTable.put(section, keyTable);
		if (newChapter)
			cache.put(chapter, sectionTable);
		return cache;
	}

	private class PropertiesFilter implements FilenameFilter {
		private PropertiesFilter() {
		}

		public boolean accept(File dir, String filename) {
			if (filename.startsWith(PropertiesSource.this.mPrefix) && filename.endsWith(".conf"))
				return true;
			return false;
		}
	}
}
