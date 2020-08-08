package regression.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import javax.naming.NamingException;
import org.xml.sax.SAXParseException;

public class XException extends Exception {
	private static Hashtable mExceptionInformation = new Hashtable<Object, Object>();
	private String mMessageText = null;

	public XException(String location, String layer, String Package, String number) {
		traceException(location, layer, Package, number, null);
	}

	public XException(String location, String layer, String Package, String number, List params) {
		traceException(location, layer, Package, number, params);
	}

	public XException(String location, String layer, String Package, String number, Throwable t, List params) {
		super(t.getMessage());
		traceException(location, layer, Package, number, t, params);
	}

	public XException(String location, String layer, String Package, String number, Throwable t) {
		super(t.getMessage());
		traceException(location, layer, Package, number, t, null);
	}

	private void traceException(String Location, String Layer, String Package, String Number, List params) {
		String basename = "errors";
		String key = String.valueOf(Location) + "_" + Layer + "_" + Package + "_" + Number;
		MessageHandler msg = MessageHandler.getInstance(basename);
		String messageText = msg.getMessage(key, params);
		if (Trace.isInitialized()) {
			Trace.error("Exception caught: " + key + " " + messageText, this);
		} else {
			System.out.println("Exception caught: " + key + " " + messageText);
			printStackTrace();
		}
		messageText = String.valueOf(key) + " " + messageText;
		setExceptionInformation(messageText, this);
		this.mMessageText = messageText;
	}

	private void traceException(String Location, String Layer, String Package, String Number, Throwable t,
			List params) {
		if (!getClass().equals(t.getClass())) {
			String basename = "errors";
			String key = String.valueOf(Location) + "_" + Layer + "_" + Package + "_" + Number;
			MessageHandler msg = MessageHandler.getInstance(basename);
			String messageText = msg.getMessageOptional(key, params);
			if (messageText == null) {
				messageText = t.getMessage();
				if (messageText == null && t.getCause() != null)
					messageText = t.getCause().getMessage();
				if (messageText == null)
					messageText = t.getClass().getName();
			}
			if (Trace.isInitialized()) {
				Trace.error("Exception caught: " + key + " " + messageText, t);
				traceAdditionalInfo(t);
			} else {
				System.out.println("Exception caught: " + key + " " + messageText);
				t.printStackTrace();
			}
			messageText = String.valueOf(key) + " " + messageText;
			setExceptionInformation(messageText, t);
			this.mMessageText = messageText;
		} else {
			this.mMessageText = t.getMessage();
		}
	}

	public static void setExceptionInformation(String message, Throwable t) {
		if (t == null) {
			mExceptionInformation.put(Thread.currentThread().getName(), message);
		} else {
			mExceptionInformation.put(Thread.currentThread().getName(),
					message + Constants.LINE_SEPERATOR + Constants.LINE_SEPERATOR + getStackTraceAsString(t));
		}
	}

	private static String getStackTraceAsString(Throwable t) {
		String retString = null;
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			PrintStream printStream = new PrintStream(outStream);
			t.printStackTrace(printStream);
			retString = outStream.toString();
			printStream.close();
			outStream.close();
		} catch (IOException iOException) {
		}
		return retString;
	}

	private void traceAdditionalInfo(Throwable t) {
		if (t instanceof NamingException) {
			Trace.error("Resolved Name\t : " + ((NamingException) t).getResolvedName());
			Trace.error("Resolved Object : " + ((NamingException) t).getResolvedObj());
			Trace.error("Remaining Name  : " + ((NamingException) t).getRemainingName());
			Trace.error("Explanation : " + ((NamingException) t).getExplanation());
		} else if (t instanceof SQLException) {
			Trace.error("SQLState : " + ((SQLException) t).getSQLState());
			Trace.error("Errorcode: " + ((SQLException) t).getErrorCode());
		} else if (t instanceof SAXParseException) {
			Trace.error("Parsing error occurred at line " + ((SAXParseException) t).getLineNumber() + ", column "
					+ ((SAXParseException) t).getColumnNumber());
		}
	}
}
