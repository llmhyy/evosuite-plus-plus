package org.evosuite.testcase.parser;

public class ParseException extends RuntimeException {

    private static final long serialVersionUID = -6037099774283337459L;

    public static final String GENERAL_ERR = "general parsing error";

    public static final String CLASS_NOT_FOUND = "could not find class";
    public static final String CONSTRUCTOR_NOT_FOUND = "could not find constructor";
    public static final String METHOD_NOT_FOUND = "could not find method";

    /**
     * <p>Constructor for ParseException.</p>
     *
     * @param reason a {@link java.lang.String} object.
     */
    public ParseException(String reason) {
        super(reason);
    }

}
