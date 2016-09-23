package com.mickey305.common.v2.util;

import java.util.List;

public interface AssertSystemMethods {
    void assertStandardOut(String... expectedLine) throws Exception;
    void assertStandardOutPattern(String... expectedRegex) throws Exception;
    void assertStandardError(String... expectedLine) throws Exception;
    void assertStandardErrorPattern(String... expectedRegex) throws Exception;

    default void assertStandardOut(List<String> expectedLine) throws Exception {
        assertStandardOut((String[]) expectedLine.toArray(new String[0]));
    }

    default void assertStandardOutPattern(List<String> expectedRegex) throws Exception {
        assertStandardOutPattern((String[]) expectedRegex.toArray(new String[0]));
    }

    default void assertStandardError(List<String> expectedLine) throws Exception {
        assertStandardError((String[]) expectedLine.toArray(new String[0]));
    }

    default void assertStandardErrorPattern(List<String> expectedRegex) throws Exception {
        assertStandardErrorPattern((String[]) expectedRegex.toArray(new String[0]));
    }
}
