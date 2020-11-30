package feature.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class Configuration {
	private static Hashtable mInstances = new Hashtable<Object, Object>();
	private static final Object classLock = Configuration.class;
	private ConfigSource mSource = null;
	private Hashtable mCache = null;

	public static Configuration getInstance() throws XException {
		return getInstance("standard");
	}

	private Configuration(String source) {
		if (Constants.XBUS_HOME == null) {
			System.out.println("I_00_000_2 XBUS_HOME has not been set!");
			System.exit(1);
		}
		this.mSource = new PropertiesSource(source);
	}

	public static Configuration getInstance(String source) throws XException {
		synchronized (classLock) {
			Configuration instance = (Configuration) mInstances.get(source);
			if (instance == null) {
				instance = new Configuration(source);
				instance.readCache();
				mInstances.put(source, instance);
			}
			return instance;
		}
	}

	public String getValue(String chapter, String section, String key) throws XException {
		String returnString = getValueInternal(chapter, section, key);
		if (returnString == null) {
			List<String> params = new Vector();
			params.add(chapter);
			params.add(section);
			params.add(key);
			throw new XException("I", "00", "000", "1", params);
		}
		return returnString;
	}

	private String getValueInternal(String chapter, String section, String key) {
		Hashtable sectTable = (Hashtable) this.mCache.get(chapter);
		if (sectTable == null)
			return null;
		Hashtable keyTable = (Hashtable) sectTable.get(section);
		if (keyTable == null)
			return null;
		return (String) keyTable.get(key);
	}

	public String getValueOptional(String chapter, String section, String key) {
		return getValueInternal(chapter, section, key);
	}

	public int getValueAsIntOptional(String chapter, String section, String key) throws XException {
		String returnString = getValueInternal(chapter, section, key);
		if (returnString == null)
			return 0;
		try {
			int retInt = Integer.parseInt(returnString);
			return retInt;
		} catch (NumberFormatException e) {
			List<String> params = new Vector();
			params.add(chapter);
			params.add(section);
			params.add(key);
			throw new XException("I", "00", "000", "3", params);
		}
	}

	public static String getClass(String type, String name) throws XException {
		return getInstance("xbus").getValue("Class", type, name);
	}

	private void readCache() throws XException {
		this.mCache = this.mSource.readCache();
		replaceVariables(this.mCache);
	}

	private void replaceVariables(Hashtable cache) throws XException {
		Map variables = getVariables(cache);
		Enumeration<Hashtable> chapters = cache.elements();
		while (chapters.hasMoreElements()) {
			Hashtable chapter = chapters.nextElement();
			Enumeration<Hashtable> sections = chapter.elements();
			while (sections.hasMoreElements()) {
				Hashtable<String, String> section = sections.nextElement();
				for (Enumeration<String> keys = section.keys(); keys.hasMoreElements();) {
					String key = keys.nextElement();
					String value = (String) section.get(key);
					int variablePosNew = value.indexOf("$VARIABLE_");
					if (variablePosNew >= 0) {
						int variablePosOld = -99999;
						String variable = null;
						Set variablesKeySet = null;
						while (variablePosNew >= 0 && variablePosOld != variablePosNew) {
							variablePosOld = variablePosNew;
							if (variables == null)
								throw new XException("I", "04", "003", "4");
							variablesKeySet = variables.keySet();
							if (variablesKeySet == null)
								throw new XException("I", "04", "003", "4");
							String variablesKey = null;
							Iterator<String> it = variablesKeySet.iterator();
							while (it.hasNext()) {
								variablesKey = it.next();
								variable = (String) variables.get(variablesKey);
								if (value.indexOf(variablesKey) >= 0)
									value = XStringSupport.replaceAll(value, variablesKey, variable);
							}
							variablePosNew = value.indexOf("$VARIABLE_");
						}
						if (variablePosOld == variablePosNew) {
							List<Integer> params = new Vector();
							params.add(new Integer(variablePosOld));
							params.add(new Integer(value));
							throw new XException("I", "04", "003", "3", params);
						}
					}
					if (value.indexOf("$XBUS_HOME$") >= 0)
						value = XStringSupport.replaceAll(value, "$XBUS_HOME$", Constants.XBUS_HOME);
					section.put(key, value);
				}
			}
		}
	}

	private Map getVariables(Hashtable cache) {
		Hashtable sections = (Hashtable) cache.get("Base");
		Map<Object, Object> variablesConf = null;
		if (sections == null)
			return null;
		Hashtable<?, ?> keys = (Hashtable) sections.get("Variable");
		if (keys != null) {
			variablesConf = new TreeMap<Object, Object>(keys);
		} else {
			return null;
		}
		Map<Object, Object> variablesNew = new Hashtable<Object, Object>();
		if (variablesConf != null) {
			String key = null;
			String variable = null;
			for (Iterator<Object> it = variablesConf.keySet().iterator(); it.hasNext();) {
				key = (String) it.next();
				variable = (String) variablesConf.get(key);
				key = "$VARIABLE_" + key + "$";
				variablesNew.put(key, variable);
			}
		}
		return variablesNew;
	}
}
