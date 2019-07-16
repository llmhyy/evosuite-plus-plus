package evosuite.shell.listmethod;

public enum MethodFilterOption {

	FLAG_PROCEDURE_METHOD ("flagProc"),
	FLAG_PROCEDURE_METHOD_WITH_SIMPLE_RETURN ("flagProcSimplRet"),
	HAS_BRANCH ("hasBranch"),
	RELEVANT_JDK_CLASSES ("relJdkClasses"),
	FLAG_METHOD_PROFILES("flagMethodProf"),
	FLAG_PRIMITIVE_PARAMETER_FIELD("primitiveParamFieldMethod"),
	NO_FLAG_METHOD("noFlag"),
	PRIMITIVE_PARAMETER("primParam");
	
	private String text;
	private MethodFilterOption(String text) {
		this.text = text;
	}
	
	public IMethodFilter getCorrespondingFilter() {
		switch(this) {
		case FLAG_PROCEDURE_METHOD:
			return new MethodFlagCondFilter();
		case FLAG_PROCEDURE_METHOD_WITH_SIMPLE_RETURN:
			return new MethodFlagCondWithSimpleReturnFilter();
		case HAS_BRANCH:
			return new MethodHasBranchFilter();
		case RELEVANT_JDK_CLASSES:
			return new ListRelevantJdkClasses();
		case FLAG_METHOD_PROFILES:
			return new FlagMethodProfilesFilter();
		case NO_FLAG_METHOD:
			return new NoFlagMethodFilter();
		case PRIMITIVE_PARAMETER:
			return new MethodPrimitiveFilter();
		case FLAG_PRIMITIVE_PARAMETER_FIELD:
			return new PrimitiveBasedFlagMethodFilter();
		}
		return null;
	}
	
	public String getText() {
		return text;
	}

	public static MethodFilterOption of(String optValue) {
		for (MethodFilterOption opt : values()) {
			if (opt.text.equals(optValue)) {
				return opt;
			}
		}
		try {
			return valueOf(optValue);
		} catch (Exception e) {
			return null;
		}
	}
}
