package feature.smartseed.example.empirical;

import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class XmlElement {
	String name;
	static List<XmlElement> subElements;
	XmlElement parent;
	private String username;

	public XmlElement() {
		subElements = new Vector();
	}

	public XmlElement(String name) {
		this.name = name;
		subElements = new Vector();
	}

	public XmlElement(String name, Hashtable<String, String> attributes) {
		this.name = name;
		subElements = new Vector();
	}

	public XmlElement(String name, String data) {
		this.name = name;
		subElements = new Vector();
	}

	public String getName() {
		return name;
	}
	
	public boolean addElement(XmlElement e) {
		return subElements.add(e);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void getElement(String path) {
		int i = path.indexOf('.');

		if (i == 0) {
			path = path.substring(1);
			i = path.indexOf('.');
		}
		String subName;
		String topName;
		if (i > 0) {
			topName = path.substring(0, i);
			subName = path.substring(i + 1);
		} else {
			topName = path;
			subName = null;
		}

		for (int j = 0; j < subElements.size(); j++) {
			if (((XmlElement) subElements.get(j)).getName().equals(topName)) {
				if (subName != null) {
					return;
				}
				return;
			}
		}
	}
	
	public void removeElement(XmlElement e) {
		XmlElement child = null;

		for (int i = 0; i < subElements.size(); i++) {
			child = (XmlElement) subElements.get(i);

			if (child == e) {
				System.currentTimeMillis();
			}
		}
	}
	
	private Set<XmlElement> accounts;
	public void getAuthenticationInfo(Principal subjectIdentity) throws IllegalAccessException, NoSuchMethodException,
			InvocationTargetException, InstantiationException, ClassNotFoundException {
		for (XmlElement entry : accounts) {
			if (entry.getUsername().equals(subjectIdentity.getName())) {
				System.currentTimeMillis();
			}
		}
		return;
	}
	
	public void setAccounts(Set<XmlElement> accounts) {
		this.accounts = accounts;
	}
}
