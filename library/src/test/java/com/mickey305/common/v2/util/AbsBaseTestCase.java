package com.mickey305.common.v2.util;

import org.junit.After;
import org.junit.Before;

public abstract class AbsBaseTestCase {
    public static final String TAG = AbsBaseTestCase.class.getSimpleName();

    @Before
    public void setUp() throws Exception { }

    @After
    public void tearDown() throws Exception { }

    /**
     * Class Tag information: e.g. class name etc.
     * @return tag
     */
    abstract public String getTag();
}
