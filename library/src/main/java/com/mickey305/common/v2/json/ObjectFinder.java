/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 K.Misaki
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
package com.mickey305.common.v2.json;

import com.mickey305.common.v2.exception.InsertObjectTypeException;
import com.mickey305.common.v2.json.model.Token;
import com.mickey305.common.v2.json.model.TokenSupplier;
import com.mickey305.common.v2.json.model.Type;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ObjectFinder<T extends Token> extends Picker<T> implements FinderManager<T> {
    public ObjectFinder(Object JsonObjectJsonArray, @NotNull TokenSupplier<T> supplier) throws InsertObjectTypeException {
        super(JsonObjectJsonArray, supplier);
    }

    /**
     *
     * @param key
     * @return
     */
    public List<T> findByKey(String key) {
        return findBy(AccessPoint.Key, key, this);
    }

    /**
     *
     * @param value
     * @return
     */
    public List<T> findByValue(String value) {
        return findBy(AccessPoint.Value, value, this);
    }

    @Override
    public Type finderMethodTargetStartSymbol() {
        return Type.START_OBJECT;
    }
}
