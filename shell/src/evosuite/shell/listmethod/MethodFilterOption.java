package evosuite.shell.listmethod;

public enum MethodFilterOption {

	FLAG_PROCEDURE_METHOD ("flagProc"),
	FLAG_PROCEDURE_METHOD_WITH_SIMPLE_RETURN ("flagProcSimplRet"),
	HAS_BRANCH ("hasBranch"),
	RELEVANT_JDK_CLASSES ("relJdkClasses"),
	FLAG_METHOD_PROFILES("flagMethodProf"),
	FLAG_PRIMITIVE_PARAMETER_FIELD("primitiveParamFieldMethod"),
	IPF_EASY_OBJECT("ipfEasyObject"),
	NO_FLAG_METHOD("noFlag"),
	PRIMITIVE_PARAMETER("primParam"),
	AT_LEAST_FOUR_BRANCHES("atLeastFourBranches"),
	PRIMITIVE_BASED_METHOD_WITH_CONSTRAINT("primitiveBasedMethodWithConstraint"),
	PRIMITIVE_BASED_METHOD("primitiveBasedMethod"),
	BRANCHED_METHOD("branchedMethod"),
	STRING_ARRAY_INPUT_METHOD("stringArrayInputMethod"),
	STRING_ARRAY_CONDITION_RELATED_METHOD("stringArrayConditionRelatedMethod"),
	PRIMITIVE_ARRAY_CONDITION_RELATED_METHOD("primitiveArrayConditionRelatedMethod"),
	MAIN_METHOD("mainMethod"),
	SMART_MUTATION_METHOD("smartMutationMethod"),
	CALLS_INT_METHOD("callsIntMethod"),
	CALLS_RECURSIVE_METHOD("callsRecursiveMethod"),
	OBJECT_CONSTRUCTION("objectConstruction"),
	EQUALS_METHOD("equalsMethod");
	
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
		case IPF_EASY_OBJECT:
			return new InterproceduralFlagMethodEasyObjectFilter();
		case AT_LEAST_FOUR_BRANCHES:
			return new MethodAtleastFourBranchesFilter();
		case PRIMITIVE_BASED_METHOD_WITH_CONSTRAINT:
			return new PrimitiveBasedWithMethodConstraintFilter();
		case PRIMITIVE_BASED_METHOD:
			return new PrimitiveBasedFilter();
		case BRANCHED_METHOD:
			return new BranchedMethodFilter();
		case STRING_ARRAY_INPUT_METHOD:
			return new StringArrayInputFilter();
		case STRING_ARRAY_CONDITION_RELATED_METHOD:
			return new StringArrayConditionRelatedFilter();
		case PRIMITIVE_ARRAY_CONDITION_RELATED_METHOD:
			return new PrimitiveArrayConditionRelatedFilter();
		case MAIN_METHOD:
			return new MainMethodFilter();
		case SMART_MUTATION_METHOD:
			return new SmartMutationFilter();
		case CALLS_INT_METHOD:
			return new CallsIntMethodFilter();
		case CALLS_RECURSIVE_METHOD:
			return new CallsRecursiveMethodFilter();
		case OBJECT_CONSTRUCTION:
			return new ObjectConstructionFilter();
		case EQUALS_METHOD:
			return new EqualsMethodFilter();
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
