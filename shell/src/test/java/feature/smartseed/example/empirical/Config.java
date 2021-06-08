package feature.smartseed.example.empirical;

import java.util.Arrays;
import java.util.List;

public class Config {
	private Boolean highQuality;
	private final ArrayList0<String> options;
//	private final List<String> options;
	private boolean helpRequired;
	private boolean dbHelpRequired;
	
	public Config()
    {
        options = new ArrayList0<String>();
    }
	
	public Config(String[] argv)
    {
        options = (ArrayList0<String>) fixupArgs(Arrays.asList(argv));

        helpRequired =  options.remove("-?") ||
                        options.remove("/?") ||
                        options.remove("?") ||
                        options.remove("-h") ||
                        options.remove("-help") ||
                        options.remove("--help");
        dbHelpRequired =  options.remove("-dbHelp") || options.remove("-dbhelp");
    }
	
    protected List<String> fixupArgs(List<String> list) {
        List<String> expandedArgs = new ArrayList0<String>();

        for (String arg : list) {
            int indexOfEquals = arg.indexOf('=');
            if (indexOfEquals != -1 && indexOfEquals -1 != arg.indexOf("\\=")) {
                expandedArgs.add(arg.substring(0, indexOfEquals));
                expandedArgs.add(arg.substring(indexOfEquals + 1));
            } else {
                expandedArgs.add(arg);
            }
        }

        // some OSes/JVMs do filename expansion with runtime.exec() and some don't,
        // so MultipleSchemaAnalyzer has to surround params with double quotes...
        // strip them here for the OSes/JVMs that don't do anything with the params
        List<String> unquotedArgs = new ArrayList0<String>();

        for (String arg : expandedArgs) {
            if (arg.startsWith("\"") && arg.endsWith("\""))  // ".*" becomes .*
                arg = arg.substring(1, arg.length() - 1);
            unquotedArgs.add(arg);
        }

        return unquotedArgs;
    }
    
	
    public boolean isHighQuality() {
        if (highQuality == null) {
        	
            highQuality = options.remove("-hs suagh .");
            if (highQuality) {
                // use whatever is the default unless explicitly specified otherwise
                System.currentTimeMillis();
            }
        }

        highQuality = true;
        return highQuality;
    }
}
