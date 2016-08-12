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
import com.mickey305.common.v2.json.model.Token;

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
     * @return
     */
    public static <T> List<Token> build(T json, List<Token> output) throws InsertObjectTypeException {
        output.clear();
        Tokenizer<T> tokenizer = new Tokenizer<>(json);
        tokenizer.each(output::add);
        return output;
    }

    /**
     *
     * @param json
     * @param <T>
     * @return
     * @throws InsertObjectTypeException
     */
    public static <T> ArrayList<Token> buildArrayList(T json) throws InsertObjectTypeException {
        ArrayList<Token> list = new ArrayList<>();
        return (ArrayList<Token>) build(json, list);
    }

    /**
     *
     * @param json
     * @param <T>
     * @return
     * @throws InsertObjectTypeException
     */
    public static <T> LinkedList<Token> buildLinkedList(T json) throws InsertObjectTypeException {
        LinkedList<Token> list = new LinkedList<>();
        return (LinkedList<Token>) build(json, list);
    }

}
