/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 K.Misaki
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.mickey305.common.v2.json.io;

import com.mickey305.common.v2.exception.InsertObjectTypeException;
import com.mickey305.common.v2.json.ObjectTypeChecker;

public class IterationUtil {
    public static final String TAG = IterationUtil.class.getName();

    private IterationUtil() {}

    /**
     *
     * @param t
     * @throws InsertObjectTypeException
     */
    public static <T> void checkObjectType(T t) throws InsertObjectTypeException {
        if(!ObjectTypeChecker.isJSONObjectOrJSONArray(t))
            throw new InsertObjectTypeException("Unexpected Object:\n"
                    + "\tplease insert the instance object of the JSONObject.class, JSONArray.class or subclass.");
    }
}
