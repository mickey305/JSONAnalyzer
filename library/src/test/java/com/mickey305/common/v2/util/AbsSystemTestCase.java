package com.mickey305.common.v2.util;

import org.jetbrains.annotations.Contract;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public abstract class AbsSystemTestCase extends AbsBaseTestCase implements AssertSystemMethods {
    public static final String TAG = AbsSystemTestCase.class.getSimpleName();

    private static final String NEW_LINE = System.lineSeparator();
    private static final UnaryOperator<String> addStartMark = data -> "^" + data;
    private static final UnaryOperator<String> addEndMark = data -> data + "$";
    private static final UnaryOperator<String> wrapWithMarks = data -> addStartMark.apply(addEndMark.apply(data));
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        System.setOut(null);
        System.setErr(null);
    }

    public ByteArrayOutputStream getOutputContent() {
        return this.outContent;
    }

    public ByteArrayOutputStream getErrorContent() {
        return this.errContent;
    }

    @Contract(pure = true)
    public static String lineSeparator() {
        return NEW_LINE;
    }

    @Override public void assertStandardOut(String... expectedLine) throws Exception {
        assertStandardStream(outContent, expectedLine);
    }

    @Override public void assertStandardOutPattern(String... expectedRegex) throws Exception {
        assertStandardStreamPattern(outContent, expectedRegex);
    }

    @Override public void assertStandardError(String... expectedLine) throws Exception {
        assertStandardStream(errContent, expectedLine);
    }

    @Override public void assertStandardErrorPattern(String... expectedRegex) throws Exception {
        assertStandardStreamPattern(errContent, expectedRegex);
    }

    private void assertStandardStream(ByteArrayOutputStream stream, String... expectedLine) throws Exception {
        String str = bindLineStrings(expectedLine);
        assertEquals(stream.toString(), str);
    }

    private void assertStandardStreamPattern(ByteArrayOutputStream stream, String... expectedRegex) throws Exception {
        Pattern pattern = Pattern.compile(wrapWithMarks.apply(bindLineStrings(expectedRegex)));
        assertThat(pattern.matcher(stream.toString()).matches(), is(true));
    }

    @Contract(pure = true)
    private String bindLineStrings(String... strings) {
        String result = "";
        for (String line : strings) { result += addReturnMark(line); }
        return  result;
    }

    @Contract(pure = true)
    private String addReturnMark(String line) {
        return line + NEW_LINE;
    }
}