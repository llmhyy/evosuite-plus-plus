package testcode.graphgeneration.model;

import java.util.Map;

/**
 * A generated code unit is an wrapper around a list of classes and their corresponding source code,
 * as well as a target method. It corresponds to a single OCG and its generated code.
 * @author Darien
 *
 */
public class GeneratedCodeUnit {
	private Map<String, String> filenameToSourceCode;
	private String targetMethodSignature;
	
	public GeneratedCodeUnit(Map<String, String> filenameToSourceCode, String targetMethodSignature) {
		this.filenameToSourceCode = filenameToSourceCode;
		this.targetMethodSignature = targetMethodSignature;
	}

	public Map<String, String> getFilenameToSourceCode() {
		return filenameToSourceCode;
	}

	public String getTargetMethodSignature() {
		return targetMethodSignature;
	}
}
