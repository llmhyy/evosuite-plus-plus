package org.evosuite.testcase.parser;

public class ParseException extends RuntimeException {

    private static final long serialVersionUID = -6037099774283337459L;

    public static final String GENERAL_ERR = "general parsing error";

    public static final String CLASS_NOT_FOUND_MSG = "could not find class";
    public static final String CTOR_NOT_FOUND_MSG = "could not find constructor";
    public static final String METHOD_NOT_FOUND_MSG = "could not find method";

    public enum ParseExceptionType {
        CLASS_NOT_FOUND,
        CTOR_NOT_FOUND,
        CTOR_ARGS_NOT_MATCHED,
        METHOD_NOT_FOUND,
        METHOD_ARGS_NOT_MATCHED,
    }

    /**
     * <p>Constructor for ParseException.</p>
     *
     * @param reason a {@link java.lang.String} object.
     */
    public ParseException(String reason) {
        super(reason);
    }

}
