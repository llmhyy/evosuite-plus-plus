package feature.smartseed.example.empirical;

import java.io.File;
import java.util.Iterator;
import java.util.StringTokenizer;

public class ResourceDescriptor extends AbstractResourceDescriptor{
	private final File source;
	private final String[] destination;
	
//	public ResourceDescriptor(WixGen wixGen, Resource resource) throws IOException {
//		this(resource.getSource(), resource.getDestination());
//		Iterator var4 = resource.getShortcuts().iterator();      
//		while(var4.hasNext()) {
//			Shortcut shortcut = (Shortcut)var4.next();
//			this.addShortcuts(new ShortcutDescriptor[]{
//					new ShortcutDescriptor(wixGen, shortcut, 
//							resource.getIcon(), true, this.getResourceID(), (String)null)});
//			}
//		}
	
	public ResourceDescriptor(File source, String destination) {
		super(destination.replaceAll("^.*/", ""));
		this.source = source;
	    if (!destination.matches("(/[a-zA-Z0-9_\\-\\.]+)+")) {
	    	throw new AssertionError("Bad resource destination: " + destination);
	    	} else {
	    		StringTokenizer st = new StringTokenizer(destination, "/");
	    		this.destination = new String[st.countTokens()];
	    		for(int i = 0; st.hasMoreTokens(); ++i) {
	    			this.destination[i] = st.nextToken();         }      }
	    }

	public File getSource() {
		return source;
	}
	public String[] getDestination() {
		return this.destination;
		}
}
