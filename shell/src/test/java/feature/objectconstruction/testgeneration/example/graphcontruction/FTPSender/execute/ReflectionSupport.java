package feature.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute;

import java.util.List;
import java.util.Vector;

public class ReflectionSupport {
	public static Object createObject(String classname) throws XException {
		Class classDefinition = classForName(classname);
		Object retObject = null;
		try {
			retObject = classDefinition.newInstance();
		} catch (InstantiationException e) {
			if (e.getCause() != null && e.getCause() instanceof XException)
				try {
					throw (XException) e.getCause();
				} catch (ClassCastException e1) {
					List<String> list = new Vector();
					list.add(classDefinition.getName());
					throw new XException("I", "00", "007", "1", e, list);
				}
			List<String> params = new Vector();
			params.add(classDefinition.getName());
			throw new XException("I", "00", "007", "1", e, params);
		} catch (IllegalAccessException e) {
			List<String> params = new Vector();
			params.add(classDefinition.getName());
			throw new XException("I", "00", "007", "2", e, params);
		}
		return retObject;
	}

	public static Class classForName(String className) throws XException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			if (cl == null || !(cl instanceof XBUSClassLoader))
				return Class.forName(className, true,
						XBUSClassLoader.getInstance(Thread.currentThread().getContextClassLoader()));
			return Class.forName(className, true, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			List<String> params = new Vector();
			params.add(className);
			throw new XException("I", "00", "007", "3", e, params);
		}
	}
}
