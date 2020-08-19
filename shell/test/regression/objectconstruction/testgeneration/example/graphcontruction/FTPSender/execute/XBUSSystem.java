package regression.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class XBUSSystem {
	private Hashtable mAddresses = new Hashtable<Object, Object>();
	private String mName = null;
	private boolean mBroadcast;
	private static Hashtable mAdditionalAddressImplementations = new Hashtable<Object, Object>();

	public String getName() {
		return this.mName;
	}

	public boolean getBroadcast() {
		return this.mBroadcast;
	}

	public void getBroadcastData(String text) throws XException {
		if (hasAdditionalAddressMarker(this.mName, text)) {
			this.mAddresses.put("AddressImplementation", "broadcast");
		} else {
			this.mBroadcast = false;
		}
	}

	private static boolean hasAdditionalAddressMarker(String systemName, String text) throws XException {
		AdditionalAddress additionalAddressImplementation = getAdditionalAddressImplementation(systemName);
		if (additionalAddressImplementation != null)
			return additionalAddressImplementation.hasMarker(text);
		return false;
	}

	public static AdditionalAddress getAdditionalAddressImplementation(String systemName) throws XException {
		if (!mAdditionalAddressImplementations.contains(systemName)) {
			Configuration config = Configuration.getInstance();
			String addressImplementationNameShort = config.getValueOptional("System", systemName,
					"AddressImplementation");
			if (addressImplementationNameShort == null)
				addressImplementationNameShort = config.getValueOptional("Base", "System", "AddressImplementation");
			if (addressImplementationNameShort != null) {
				String addressImplementationName = Configuration.getClass("AdressImplementation",
						addressImplementationNameShort);
				mAdditionalAddressImplementations.put(systemName,
						ReflectionSupport.createObject(addressImplementationName));
			}
		}
		return (AdditionalAddress) mAdditionalAddressImplementations.get(systemName);
	}

	public String[] replaceAllMarkers(String text) throws XException {
		text = XStringSupport.replaceAll(text, "$TIMESTAMP$", Constants.getDateAsString());
		String key = null;
		String marker = null;
		for (Enumeration<String> e = this.mAddresses.keys(); e.hasMoreElements();) {
			key = e.nextElement();
			if (!key.equals("AddressImplementation") && !key.equals("$WILDCARD$")) {
				marker = "$" + key + "$";
				if (text.indexOf(marker) >= 0)
					text = text.replaceAll(marker, (String) this.mAddresses.get(key));
			}
		}
		if (this.mAddresses.containsKey("$WILDCARD$"))
			if (text.indexOf("$WILDCARD$") >= 0)
				text = XStringSupport.replaceAll(text, "$WILDCARD$", (String) this.mAddresses.get("$WILDCARD$"));
		if (this.mAddresses.containsKey("AddressImplementation")) {
			AdditionalAddress addAddress = getAdditionalAddressImplementation(getName());
			if (addAddress.hasMarker(text)) {
				if (this.mBroadcast) {
					List<String> addAddresses = addAddress.getAddresses();
					int quantAddresses = addAddresses.size();
					if (quantAddresses < 1) {
						List<String> params = new Vector();
						params.add(this.mName);
						params.add(text);
						throw new XException("I", "04", "003", "5", params);
					}
					String[] arrayOfString2 = new String[quantAddresses];
					for (int i = 0; i < quantAddresses; i++)
						arrayOfString2[i] = addAddress.replaceMarker(text, addAddresses.get(i));
					return arrayOfString2;
				}
				String[] arrayOfString1 = {
						addAddress.replaceMarker(text, (String) this.mAddresses.get("AddressImplementation")) };
				return arrayOfString1;
			}
			String[] arrayOfString = { text };
			return arrayOfString;
		}
		String[] result = { text };
		return result;
	}
}
