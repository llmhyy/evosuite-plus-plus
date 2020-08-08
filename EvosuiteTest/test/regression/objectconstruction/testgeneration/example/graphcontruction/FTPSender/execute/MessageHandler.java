package regression.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class MessageHandler {
	private static Hashtable mInstances = new Hashtable<Object, Object>();
	private static final Object classLock = Configuration.class;
	private Hashtable mMessages = null;

	public String getMessage(String key, List params) {
		String messageText = null;
		messageText = getMessageOptional(key, params);
		if (messageText == null) {
			messageText = "Key: " + key + " not found in message file";
			Trace.error(messageText);
		}
		return messageText;
	}

	public String getMessageOptional(String key, List params) {
		String messageText = null;
		if (this.mMessages == null)
			return null;
		messageText = (String) this.mMessages.get(key);
		if (messageText == null)
			return null;
		int counter = 1;
		if (params != null) {
			String paramText = null;
			Object paramObject = null;
			for (Iterator it = params.iterator(); it.hasNext();) {
				paramObject = it.next();
				if (paramObject != null) {
					paramText = paramObject.toString();
				} else {
					paramText = "<null>";
				}
				String paramCounter = "$" + counter + "$";
				if (messageText.indexOf(paramCounter) >= 0) {
					messageText = XStringSupport.replaceAll(messageText, paramCounter, paramText);
					counter++;
				}
			}
		}
		return messageText;
	}

	private MessageHandler(String basename) {
		this.mMessages = new Hashtable<Object, Object>();
		addMessages(basename, Constants.XBUS_ETC);
		addMessages(basename, Constants.XBUS_PLUGIN_ETC);
	}

	private void addMessages(String basename, String dir) {
		Locale locale = Locale.ENGLISH;
		String prefix = basename;
		String postfix = "_" + locale.toString() + ".properties";
		File dirFile = new File(dir);
		String[] messagesFiles = dirFile.list(new MessagesFilter(prefix, postfix));
		for (int i = 0; messagesFiles != null && i < messagesFiles.length; i++) {
			Properties newProps = new Properties();
			try {
				FileInputStream instream = new FileInputStream(String.valueOf(dir) + messagesFiles[i]);
				newProps.load(instream);
				instream.close();
			} catch (FileNotFoundException e) {
				System.out.println("Cannot find messagefile");
				System.exit(1);
			} catch (IOException e) {
				System.out.println("Cannot find messagefile");
				System.exit(1);
			}
			String key = null;
			for (Enumeration<Object> keys = newProps.keys(); keys.hasMoreElements();) {
				key = (String) keys.nextElement();
				this.mMessages.put(key, newProps.get(key));
			}
		}
	}

	public static MessageHandler getInstance(String basename) {
		synchronized (classLock) {
			MessageHandler instance = (MessageHandler) mInstances.get(basename);
			if (instance == null) {
				instance = new MessageHandler(basename);
				mInstances.put(basename, instance);
			}
			return instance;
		}
	}

	private static class MessagesFilter implements FilenameFilter {
		private String mPrefix = null;

		private String mPostfix = null;

		public MessagesFilter(String prefix, String postfix) {
			this.mPrefix = prefix;
			this.mPostfix = postfix;
		}

		public boolean accept(File dir, String filename) {
			if (filename.startsWith(this.mPrefix) && filename.endsWith(this.mPostfix))
				return true;
			return false;
		}
	}
}
