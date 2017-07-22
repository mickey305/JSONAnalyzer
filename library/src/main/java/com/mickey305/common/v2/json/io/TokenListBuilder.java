/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 - 2017 K.Misaki
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
import com.mickey305.common.v2.json.model.Token;
import com.mickey305.common.v2.json.model.TokenSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TokenListBuilder {
    public static final String TAG = TokenListBuilder.class.getName();

    private TokenListBuilder() {}

    /**
     *
     * @param json {@link org.json.JSONObject} or {@link org.json.JSONArray}
     * @param output
     * @param supplier
     * @param <T>
     * @return
     * @throws InsertObjectTypeException
     */
    public static <T extends Token> List<T> build(
            Object json, List<T> output, @NotNull TokenSupplier<T> supplier) throws InsertObjectTypeException {
        output.clear();
        Tokenizer<T> tokenizer = new Tokenizer<>(json, supplier);
        tokenizer.each(output::add);
        return output;
    }

    /**
     *
     * @param json {@link org.json.JSONObject} or {@link org.json.JSONArray}
     * @param supplier
     * @param <T>
     * @return
     * @throws InsertObjectTypeException
     */
    public static <T extends Token> ArrayList<T> buildArrayList(
            Object json, TokenSupplier<T> supplier) throws InsertObjectTypeException {
        ArrayList<T> list = new ArrayList<>();
        return (ArrayList<T>) build(json, list, supplier);
    }

    /**
     *
     * @param json {@link org.json.JSONObject} or {@link org.json.JSONArray}
     * @param supplier
     * @param <T>
     * @return
     * @throws InsertObjectTypeException
     */
    public static <T extends Token> LinkedList<T> buildLinkedList(
            Object json, TokenSupplier<T> supplier) throws InsertObjectTypeException {
        LinkedList<T> list = new LinkedList<>();
        return (LinkedList<T>) build(json, list, supplier);
    }

}
