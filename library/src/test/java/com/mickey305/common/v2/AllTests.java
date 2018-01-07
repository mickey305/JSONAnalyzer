package com.mickey305.common.v2;

import com.mickey305.common.v2.json.PickerTest;
import com.mickey305.common.v2.json.io.TokenListBuilderTest;
import com.mickey305.common.v2.json.io.TokenizerTest;
import com.mickey305.common.v2.json.model.TokenTest;
import com.mickey305.common.v2.json.model.TypeTreeFactoryTest;
import com.mickey305.common.v2.string.ScannerLineTest;
import com.mickey305.common.v2.util.CollectibleIteratorTest;
import com.mickey305.common.v2.util.LogTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TokenizerTest.class,
        TokenListBuilderTest.class,
        TokenTest.class,
        TypeTreeFactoryTest.class,
        PickerTest.class,
        ScannerLineTest.class,
        CollectibleIteratorTest.class,
        LogTest.class
})
public class AllTests {
    // nop
}
