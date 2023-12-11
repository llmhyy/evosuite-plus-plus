package org.evosuite.lm;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.edit.EditChoice;
import com.theokanning.openai.edit.EditRequest;
import com.theokanning.openai.service.OpenAiService;
import org.evosuite.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAiLanguageModel {

    private String testSrc;
    private String authorizationKey;
    private String modelId;
    private String logPath;
    private int maxQueryLen;
    private double temperature;
    private int numCalls;
    private float timeCalling;
    private List<Integer> tokenLenCache;

    private static final Logger logger = LoggerFactory.getLogger(OpenAiLanguageModel.class);

    public OpenAiLanguageModel() {
        authorizationKey = Properties.OPENAI_AUTH_KEY;
        modelId = Properties.OPENAI_MODEL;
        temperature = 1.0;
        maxQueryLen = 16384 - 200;
        numCalls = 0;
        timeCalling = 0;
        tokenLenCache = new ArrayList<>();
    }

    // main method that sends out requests to OpenAI
    private String callChatCompletion(List<ChatMessage> messages) {
        OpenAiService service = new OpenAiService(authorizationKey, Duration.ofSeconds(100L));
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelId)
                .messages(messages)
                .maxTokens(1000)
                .temperature(temperature)
                .build();

        long startTime = System.currentTimeMillis();

        List<ChatCompletionChoice> choices = service.createChatCompletion(request).getChoices();

        timeCalling += System.currentTimeMillis() - startTime;
        numCalls += 1;

        String response = choices.get(0).getMessage().getContent();
        System.out.println("==================================================");
        System.out.println("RESPONSE:");
        System.out.println(response);
        System.out.println("==================================================");
        return response;
    }

    private String callChatCompletion(String findPrompt) {
        List<ChatMessage> messages = new ArrayList<>();
        String response;

        messages.add(new ChatMessage("user", findPrompt));
        response = callChatCompletion(messages);

        return response;
    }

    private String callChatCompletionFormat(String findPrompt, String formatPrompt) {
        List<ChatMessage> messages = new ArrayList<>();
        String response;

        messages.add(new ChatMessage("user", findPrompt));
        response = callChatCompletion(messages);
        messages.add(new ChatMessage("assistant", response));

        messages.add(new ChatMessage("user", formatPrompt));
        response = callChatCompletion(messages);
//        System.out.println("RESPONSE:\n" + response);
        response = extractCodeSnippet(response);

        try {
//            System.out.println(System.currentTimeMillis());
            Thread.sleep(15000);
//            System.out.println(System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response;
    }

    public String getInitialPopulation(String targetMethod, String targetSummary) {
        String initPrompt =
                String.format("Write unit tests to cover all branches of %s without using any mocking.\n", targetMethod) +
                wrapCodeBlock(targetSummary);
        String formatPrompt = "Combine them into a test suite with the following format:\n" +
                "```java\n" +
                "public class TestSuite {\n" +
                "@Before\n" +
                "public void setUp() { INITIALIZATION_OF_TEST_CASES }\n" +
                "@Test\n" +
                "public void TEST_CASE_1() { }\n" +
                "@Test\n" +
                "public void TEST_CASE_2() { }\n" +
                "@After\n" +
                "public void tearDown() { RESET_OF_TEST_CASES }\n" +
                "}\n" +
                "```";
        System.out.println("==================================================");
        System.out.println("getInitialPopulation() is called");
        System.out.println("TARGET_METHOD="+targetMethod);
        System.out.println(initPrompt);
        System.out.println("==================================================");
        String testCases = callChatCompletion(initPrompt);
        return extractCodeSnippet(testCases);
    }

    public String fixClassNotFound(String testSuite, String className) {
        String prompt = String.format(
                "The class %s does not exist." +
                        "Modify the test suite below to be valid.\n\n" +
                        "Test suite:\n" +
                        "%s\n\n",
                className, wrapCodeBlock(testSuite));
        System.out.println("==================================================");
        System.out.println("fixClassNotFound() is called");
        System.out.println("CLASS_NOT_FOUND="+className);
        System.out.println("==================================================");
        String testCases = callChatCompletion(prompt);
        return extractCodeSnippet(testCases);
    }

    public String fixConstructorNotFound(String testSuite, String className, String classDefinition) {
        String prompt = String.format(
                "Below is the definition of %s class. " +
                        "Based on that, modify the test suite below to use %s constructors correctly.\n\n" +
                        "Test suite:\n" +
                        "%s\n\n" +
                        "Definition of %s class:\n" +
                        "%s\n",
                className, className,
                wrapCodeBlock(testSuite),
                className, wrapCodeBlock(classDefinition));
        System.out.println("==================================================");
        System.out.println("fixConstructorNotFound() is called");
        System.out.println("CTOR_NOT_FOUND="+className);
        System.out.println("==================================================");
        String testCases = callChatCompletion(prompt);
        return extractCodeSnippet(testCases);
    }

    public String fixMethodNotFound(String testSuite, String className, String methodName, String classDefinition) {
        String prompt = String.format(
                "Below is the definition of %s class. " +
                        "Based on that, modify the test suite below to use the method %s() correctly.\n\n" +
                        "Test suite:\n" +
                        "%s\n\n" +
                        "Definition of %s class:\n" +
                        "%s\n",
                className, methodName,
                wrapCodeBlock(testSuite),
                className, wrapCodeBlock(classDefinition));
        System.out.println("==================================================");
        System.out.println("fixMethodNotFound() is called");
        System.out.println("METHOD_NOT_FOUND="+className+"#"+methodName);
        System.out.println("==================================================");
        String testCases = callChatCompletion(prompt);
        return extractCodeSnippet(testCases);
    }

    public String coverNewBranch(String testSuite, String newBranch,
                                 String targetClassName, String targetClassDefinition,
                                 String newClassName, String newClassDefinition) {
        String prompt = String.format(
                "Below is the definition of %s class and %s class. " +
                        "Based on that, modify the test suite below to cover the branch `%s`.\n\n" +
                        "Test suite:\n" +
                        "%s\n\n" +
                        "Definition of %s class:\n" +
                        "%s\n\n" +
                        "Definition of %s class:\n" +
                        "%s\n",
                targetClassName, newClassName, newBranch,
                wrapCodeBlock(testSuite),
                targetClassName, wrapCodeBlock(targetClassDefinition),
                newClassName, wrapCodeBlock(newClassDefinition));
        System.out.println("==================================================");
        System.out.println("coverNewBranch() is called");
        System.out.println("BRANCH_TO_COVER="+newBranch);
        System.out.println("==================================================");
        String testCases = callChatCompletion(prompt);
        return extractCodeSnippet(testCases);
    }

    public String coverNewBranch(String targetMethod, String targetSummary, List<String> uncoveredBranches) {
        StringBuilder uncoveredBranchesStr = new StringBuilder();
        for (String b : uncoveredBranches) {
            uncoveredBranchesStr.append("`");
            uncoveredBranchesStr.append(b);
            uncoveredBranchesStr.append("`, ");
        }

        String prompt = String.format(
                "Below is the definition of %s method. " +
                        "Based on that, write a test suite to cover the branches %s without using any mocking and assertion.\n\n" +
                        "Definition of %s method:\n" +
                        "%s\n",
                targetMethod,
                uncoveredBranchesStr,
                targetMethod,
                wrapCodeBlock(targetSummary));
        System.out.println("==================================================");
        System.out.println("coverNewBranch() is called");
        System.out.println("BRANCH_TO_COVER="+uncoveredBranchesStr);
        System.out.println("==================================================");
        String testCases = callChatCompletion(prompt);
        System.out.println(testCases);
        return extractCodeSnippet(testCases);
    }

    public String coverNewBranch(String targetMethod, String targetSummary,
                                 List<String> uncoveredBranches,
                                 Map<String, String> relatedClasses) {
        StringBuilder uncoveredBranchesStr = new StringBuilder();
        for (String b : uncoveredBranches) {
            uncoveredBranchesStr.append("`");
            uncoveredBranchesStr.append(b);
            uncoveredBranchesStr.append("`, ");
        }

        StringBuilder relatedClassesStr = new StringBuilder();
        for (String relatedClass : relatedClasses.keySet()) {
            String definition = relatedClasses.get(relatedClass);
            relatedClassesStr.append(String.format("Definition of class %s:\n", relatedClass));
            relatedClassesStr.append(wrapCodeBlock(definition));
            relatedClassesStr.append("\n\n");
        }

        String prompt = String.format(
                "Below is the definition of %s method and all related classes. " +
                        "Based on that, write a test suite to cover the branches %s without using any mocking.\n\n" +
                        "Definition of %s method:\n" + "%s\n" +
                        "Definitions of all related classes:\n\n" + "%s\n",
                targetMethod,
                uncoveredBranchesStr,
                targetMethod, wrapCodeBlock(targetSummary),
                relatedClassesStr);
        System.out.println("==================================================");
        System.out.println("coverNewBranch() is called");
        System.out.println("BRANCH_TO_COVER="+uncoveredBranchesStr);
        System.out.println("==================================================");
        String testCases = callChatCompletion(prompt);
        System.out.println(testCases);
        return extractCodeSnippet(testCases);
    }

    /**
     * Tries to get the maximal source context that
     * includes start_line to end_line but
     * remains under the threshold.
     * @param startLine the start line that should be included
     * @param endLine the end line that should be included
     * @param usedTokens the number of tokens to reduce the max allowed by
     * @return as many lines from the source as possible that fit in max_context.
     */
    private String getMaximalSourceContent(int startLine, int endLine, int usedTokens) {
        String[] splitSrc = testSrc.split("\n");
        int numLines = splitSrc.length;

        if (endLine == -1) {
            endLine = numLines;
        }

        // Return everything if you can
        int numTokens = 0;
        for (int i = 0; i < numLines; i++) {
            numTokens += getNumTokensAtLine(i);
        }
        if (numTokens < maxQueryLen) {
            return testSrc;
        }

        numTokens = 0;
        for (int i = 0; i < endLine; i++) {
            numTokens += getNumTokensAtLine(i);
        }
        if (numTokens < maxQueryLen) {
            return testSrc.substring(0, endLine);
        }

        // Otherwise greedily take the lines preceding the end line
        List<Integer> cumLenOfPrefix = new ArrayList<>();
        int cumLen = 0;
        for (int i = endLine - 1; i >= 0; i--) {
            int tokenLen = getNumTokensAtLine(i);
            cumLen += tokenLen;
            cumLenOfPrefix.add(0, cumLen);
        }

        int contextStartLine = 0;
        for (int i = 0; i < cumLenOfPrefix.size(); i++) {
            int cumTokenLen = cumLenOfPrefix.get(i);
            if (cumTokenLen < maxQueryLen - usedTokens) {
                contextStartLine = i;
                break;
            }
        }

        return String.join("\n", testSrc.substring(contextStartLine, endLine));
    }

    /**
     * Asks the model to fill in the `??` in the given function.
     * @param functionToMutate a string containing code with a `??` placeholder
     * @return the result of calling the model to edit the given code.
     */
    public String callMutate(String functionToMutate) {
        String context = "";

        OpenAiService service = new OpenAiService(authorizationKey);

        EditRequest request = EditRequest.builder()
                .model(modelId)
                .input(context + "\n" + functionToMutate)
                .instruction("Fill in the ??")
                .temperature(temperature)
                .build();

        long startTime = System.currentTimeMillis();

        List<EditChoice> choices = service.createEdit(request).getChoices();

        timeCalling += System.currentTimeMillis() - startTime;
        numCalls += 1;

        return choices.get(0).getText();
    }

    /**
     * Asks the model to provide a completion of the given function header,
     * with the additional context of the target function definition.
     * @param functionHeader a string containing a def statement to be completed
     * @param contextStart the start line of context that must be included
     * @param contextEnd the end line of context that must be included
     * @return the result of calling the model to complete the function header.
     */
    public String callCompletion(String functionHeader, int contextStart, int contextEnd) {
        String context = getMaximalSourceContent(contextStart, contextEnd, 0);

        OpenAiService service = new OpenAiService(authorizationKey);

        // We want to stop the generation before
        // it spits out a bunch of other tests,
        // because that slows things down
        CompletionRequest request = CompletionRequest.builder()
                .model(modelId)
                .prompt(functionHeader + "\n" + context)
                .maxTokens(1000)
                .temperature(temperature)
                //.stop(Arrays.asList("\npublic"))
                .build();

        long startTime = System.currentTimeMillis();

        List<CompletionChoice> choices = service.createCompletion(request).getChoices();

        timeCalling += System.currentTimeMillis() - startTime;
        numCalls += 1;

        return choices.get(0).getText();
    }

    public String callChatCompletion(String functionHeader, int contextStart, int contextEnd) {
        String context = getMaximalSourceContent(contextStart, contextEnd, functionHeader.length());

        OpenAiService service = new OpenAiService(authorizationKey);

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user", functionHeader));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelId)
                .messages(messages)
                .maxTokens(1000)
                .temperature(temperature)
                //.stop(new ArrayList<>())
                .build();

        long startTime = System.currentTimeMillis();

        List<ChatCompletionChoice> choices = service.createChatCompletion(request).getChoices();

        timeCalling += System.currentTimeMillis() - startTime;
        numCalls += 1;

        return choices.get(0).getMessage().getContent();
    }

    /**
     * Asks the model to provide a completion of the method signature
     * given the simple class name and method name.
     * @param simpleClassName the simple class name of the called on class/object
     * @param simpleMethodName the simple name of the invoked method
     * @return the result of calling the model to complete the function signature.
     */
    public String findMethodSignature(String simpleClassName, String simpleMethodName) {
        String prompt = "return the full package name and its signature of method " +
                simpleMethodName + "() called on an " +
                simpleClassName + " object in Java bytecode";

        OpenAiService service = new OpenAiService(authorizationKey);

        CompletionRequest request = CompletionRequest.builder()
                .model(modelId)
                .prompt(prompt)
                .maxTokens(100)
                .temperature(temperature)
                .build();

        long startTime = System.currentTimeMillis();

        List<CompletionChoice> choices = service.createCompletion(request).getChoices();

        timeCalling += System.currentTimeMillis() - startTime;
        numCalls += 1;

        return choices.get(0).getText();
    }

    /**
     * Get the approximate number of tokens for the source file at line_num.
     * @param lineNum the line number to get the number of tokens for
     * @return the approximate number of tokens.
     */
    private int getNumTokensAtLine(int lineNum) {
        String[] lines = testSrc.split("\n");
        if (tokenLenCache.size() == 0) {
            for (String line : lines) {
                int tokenLen = approxNumberTokens(line);
                tokenLenCache.add(tokenLen);
            }
        }
        return tokenLenCache.get(lineNum);
    }

    /**
     * We want to estimate the number of tokens in a line of code.
     * From <a href="https://beta.openai.com/tokenizer">...</a> it looks like roughly
     * sequential whitespace becomes a single token, and a new token
     * is created when character "class" changes.
     * @param line a line to get the approximate number of tokens for
     * @return an approximate number of tokens in `line`.
     */
    private int approxNumberTokens(String line) {
        List<String> tokens = new ArrayList<>();
        String lastType = "other";
        String curToken = "";
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            String curType = charType(c);
            if (!curType.equals(lastType)) {
                tokens.add(curToken);
                lastType = curType;
                curToken = Character.toString(c);
            } else {
                curToken += c;
            }
        }
        return tokens.size();
    }

    private String charType(char c) {
        if (Character.isLetter(c)) {
            return "letter";
        } else if (Character.isDigit(c)) {
            return "digit";
        } else if (Character.isWhitespace(c)) {
            return "whitespace";
        } else if (Pattern.matches("\\p{Punct}", Character.toString(c))) {
            return "punctuation";
        } else {
            return "other";
        }
    }

    public String wrapCodeBlock(String codeString) {
        return String.format("```java\n%s\n```\n", codeString);
    }

    public String extractCodeSnippet(String response) {
        String codeSnippet = "";
        try {
            codeSnippet = response.substring(response.indexOf("```java") + 7);
            codeSnippet = codeSnippet.substring(0, codeSnippet.indexOf("```"));
        } catch (StringIndexOutOfBoundsException e) {
//            System.out.println(response);
            com.github.javaparser.ParseResult<CompilationUnit> result = new JavaParser().parse(response);
            boolean isSuccessful = result.isSuccessful() && result.getResult().isPresent();
            if (isSuccessful) {
                return codeSnippet;
            } else {
                return "";
            }
        }
        return codeSnippet;
    }

    public String getTestSrc() {
        return this.testSrc;
    }

    public String getAuthorizationKey() {
        return this.authorizationKey;
    }

    public String getModelId() {
        return this.modelId;
    }

    public double getTemperature() {
        return this.temperature;
    }

    public void setTestSrc(String testSrc) {
        this.testSrc = testSrc;
    }

    public void setAuthorizationKey(String authorizationKey) {
        this.authorizationKey = authorizationKey;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
