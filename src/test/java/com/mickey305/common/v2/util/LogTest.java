package com.mickey305.common.v2.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class LogTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(null);
        System.setErr(null);
    }

    @Test
    public void i() throws Exception {
        String headerRegex = "^([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-2]?[0-9]):([0-9]{2}):([0-9]{2})\\.([0-9]{3}) - ";
        Pattern pattern = Pattern.compile(headerRegex + "default message test\n");

        Log.i("default message test");

        assertThat(pattern.matcher(outContent.toString()).matches(), is(true));
    }

    @Test
    public void e() throws Exception {
        String headerRegex = "^([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-2]?[0-9]):([0-9]{2}):([0-9]{2})\\.([0-9]{3}) - ";
        Pattern pattern = Pattern.compile(headerRegex + "error message test\n");

        Log.e("error message test");

        assertThat(pattern.matcher(errContent.toString()).matches(), is(true));
    }

}