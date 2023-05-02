package org.evosuite.languagemodels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.edit.EditChoice;
import com.theokanning.openai.edit.EditRequest;
import com.theokanning.openai.service.OpenAiService;

public class OpenAILanguageModel {

    private String testSrc;
    private String authorizationKey;
    private String completeModel;
    private String editModel;
    private String logPath;
    private int maxQueryLen;
    private double temperature;
    private List<Integer> tokenLenCache;
    public int numCodexCalls;
    public float timeCallingCodex;

    public OpenAILanguageModel() {
        maxQueryLen = 4000 - 200;
        tokenLenCache = new ArrayList<>();
        numCodexCalls = 0;
        timeCallingCodex = 0;
        // TODO(vani): refactor
        setAuthorizationKey("INSERT_YOUR_OPEN_AI_TOKEN");
        setCompleteModel("text-davinci-003");
        setEditModel("code-davinci-edit-001");
        setTemperature(1.0);
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
                .model(editModel)
                .input(context + "\n" + functionToMutate)
                .instruction("Fill in the ??")
                .temperature(temperature)
                .build();

        long startTime = System.currentTimeMillis();

        List<EditChoice> choices = service.createEdit(request).getChoices();

        timeCallingCodex += System.currentTimeMillis() - startTime;
        numCodexCalls += 1;

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

        System.out.println(context);

        OpenAiService service = new OpenAiService(authorizationKey);

        // We want to stop the generation before
        // it spits out a bunch of other tests,
        // because that slows things down
        CompletionRequest request = CompletionRequest.builder()
                .model(completeModel)
                .prompt(context + "\n" /*+ functionHeader*/)
                .maxTokens(300)
                .temperature(temperature)
                //.stop(Arrays.asList("\npublic"))
                .build();

        long startTime = System.currentTimeMillis();

        List<CompletionChoice> choices = service.createCompletion(request).getChoices();

        timeCallingCodex += System.currentTimeMillis() - startTime;
        numCodexCalls += 1;

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

    public String getTestSrc() {
        return this.testSrc;
    }

    public String getAuthorizationKey() {
        return this.authorizationKey;
    }

    public String getCompleteModel() {
        return this.completeModel;
    }

    public String getEditModel() {
        return this.editModel;
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

    public void setCompleteModel(String completeModel) {
        this.completeModel = completeModel;
    }

    public void setEditModel(String editModel) {
        this.editModel = editModel;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
