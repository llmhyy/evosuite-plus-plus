package feature.objectconstruction.testgeneration.example.graphcontruction.FTPSender.execute;

public class XStringSupport {
	public static String replaceAll(String text, String marker, String replacement) {
		StringBuffer work = new StringBuffer(text);
		int pos;
		while ((pos = text.indexOf(marker)) >= 0) {
			work.replace(pos, pos + marker.length(), replacement);
			text = new String(work);
		}
		return new String(work);
	}
}
