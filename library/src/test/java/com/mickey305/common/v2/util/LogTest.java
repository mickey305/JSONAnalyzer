package com.mickey305.common.v2.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LogTest extends AbsSystemTestCase {
    public static final String TAG = LogTest.class.getSimpleName();
    private static final String REGEX = "([0-9]{4})-([0-9]{2})-([0-9]{2}) ([0-2]?[0-9]):([0-9]{2}):([0-9]{2})\\.([0-9]{3}) - ";

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Test
    public void i() throws Exception {
        List<String> testDataList;
        List<String> regexList = new ArrayList<>();

        testDataList = createSystemOutDemoData();
        testDataList.forEach(line -> regexList.add(REGEX + line));

        testDataList.forEach(Log::i);

        assertStandardOutPattern(regexList);
    }

    @Test
    public void e() throws Exception {
        List<String> testDataList;
        List<String> regexList = new ArrayList<>();

        testDataList = createSystemErrDemoData();
        testDataList.forEach(line -> regexList.add(REGEX + line));

        testDataList.forEach(Log::e);

        assertStandardErrorPattern(regexList);
    }

    // execute - Log#i()
    private List<String> createSystemOutDemoData() {
        List<String> list = new ArrayList<>();

        list.add("test default message 1");
        list.add("test default message 2");
        list.add("test default message 3");
        list.add("test default message 4");

        return list;
    }

    // execute - Log#e()
    private List<String> createSystemErrDemoData() {
        List<String> list = new ArrayList<>();

        list.add("test error message 1");
        list.add("test error message 2");
        list.add("test error message 3");
        list.add("test error message 4");

        return list;
    }
}