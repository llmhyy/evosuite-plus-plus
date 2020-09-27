package regression.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

public class XBUSClassLoader extends URLClassLoader {
	private static XBUSClassLoader mClassLoader = null;
	private static final Object classLock = XBUSClassLoader.class;

	private XBUSClassLoader(URL[] urlArray, ClassLoader parent) {
		super(urlArray, parent);
	}

	public static XBUSClassLoader getInstance(ClassLoader parent) {
		return createClassLoader(parent);
	}

	private static XBUSClassLoader createClassLoader(ClassLoader parent) {
		synchronized (classLock) {
			if (mClassLoader == null) {
				if (Constants.XBUS_HOME == null) {
					Trace.error("XBUS_HOME has not been set!");
					System.exit(1);
				}
				Vector<URL> urls = new Vector();
				addUrls(urls, String.valueOf(Constants.XBUS_HOME) + "/lib");
				addUrls(urls, String.valueOf(Constants.XBUS_HOME) + "/lib/runtime");
				addUrls(urls, String.valueOf(Constants.XBUS_HOME) + "/plugin/lib");
				addUrls(urls, String.valueOf(Constants.XBUS_HOME) + "/test/lib");
				URL[] urlArray = new URL[urls.size()];
				for (int i = 0; i < urls.size(); i++)
					urlArray[i] = urls.elementAt(i);
				mClassLoader = new XBUSClassLoader(urlArray, parent);
			}
		}
		return mClassLoader;
	}

	private static void addUrls(Vector<URL> urls, String dirName) {
		File libPath = new File(dirName);
		File[] jars = libPath.listFiles();
		for (int i = 0; jars != null && i < jars.length; i++) {
			if (jars[i].isFile() && (jars[i].getName().endsWith("jar") || jars[i].getName().endsWith("zip")))
				try {
					urls.add(jars[i].toURL());
				} catch (MalformedURLException e) {
					Trace.error(e);
					System.exit(1);
				}
		}
	}
}
