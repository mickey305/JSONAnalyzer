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
package com.mickey305.common.v2.json.model;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenUtil {
    public static final String TAG = TokenUtil.class.getName();

    public static final char CHAR_DOT   = 46; // .
    public static final char CHAR_PLUS  = 43; // +
    public static final char CHAR_MINUS = 45; // -

    public static final char CHAR_COMMA = 44; // ,
    public static final char CHAR_COLON = 58; // :

    public static final char CHAR_START_ARRAY   = 91;  // [
    public static final char CHAR_END_ARRAY     = 93;  // ]
    public static final char CHAR_START_OBJECT  = 123; // {
    public static final char CHAR_END_OBJECT    = 125; // }
    public static final char CHAR_DOUBLE_QUOTES = 34;  // "

    public static final String STRING_NULL  = "null";
    public static final String STRING_TRUE  = "true";
    public static final String STRING_FALSE = "false";

    private static final Character[] figurePartsArray = {
        CHAR_DOT, CHAR_PLUS, CHAR_MINUS
    };
    private static final Character[] operatorArray = {
        CHAR_PLUS, CHAR_MINUS
    };
    private static final Character[] symbolArray = {
        CHAR_START_ARRAY, CHAR_START_OBJECT, CHAR_END_ARRAY, CHAR_END_OBJECT
    };

    private static final List<Character> figurePartsList = Arrays.asList(figurePartsArray);
    private static final List<Character> operatorList = Arrays.asList(operatorArray);
    private static final List<Character> symbolList = Arrays.asList(symbolArray);

    private TokenUtil() {}

    public static Map<String, TYPE> getTokenMap() {
        Map<String, TYPE> map = new HashMap<>();
        map.put(String.valueOf(CHAR_END_ARRAY), TYPE.END_ARRAY);
        map.put(String.valueOf(CHAR_END_OBJECT), TYPE.END_OBJECT);
        map.put(String.valueOf(CHAR_START_ARRAY), TYPE.START_ARRAY);
        map.put(String.valueOf(CHAR_START_OBJECT), TYPE.START_OBJECT);
        map.put(STRING_FALSE, TYPE.VALUE_FALSE);
        map.put(STRING_TRUE, TYPE.VALUE_TRUE);
        map.put(STRING_NULL, TYPE.VALUE_NULL);
        return map;
    }

    public static Map<TYPE, TYPE> getSymbolMap() {
        Map<TYPE, TYPE> map = new HashMap<>();
        map.put(TYPE.START_ARRAY, TYPE.END_ARRAY);
        map.put(TYPE.START_OBJECT, TYPE.END_OBJECT);
        map.put(TYPE.END_ARRAY, TYPE.START_ARRAY);
        map.put(TYPE.END_OBJECT, TYPE.START_OBJECT);
        return map;
    }

    public static Map<TYPE, TYPE> getConversionMap() {
        Map<TYPE, TYPE> map = new HashMap<>();
        map.put(TYPE.START_OBJECT, TYPE.VALUE_JSON_OBJECT);
        map.put(TYPE.END_OBJECT, TYPE.VALUE_JSON_OBJECT);
        map.put(TYPE.START_ARRAY, TYPE.VALUE_JSON_ARRAY);
        map.put(TYPE.END_ARRAY, TYPE.VALUE_JSON_ARRAY);
        return map;
    }

    /**
     *
     * @param ch
     * @return
     */
    public static boolean isFigureParts(char ch) {
        return figurePartsList.contains(ch);
    }

    /**
     *
     * @param ch
     * @return
     */
    public static boolean isOperator(char ch) {
        return operatorList.contains(ch);
    }

    /**
     *
     * @param ch
     * @return
     */
    public static boolean isSymbol(char ch) {
        return symbolList.contains(ch);
    }

    @Contract(pure = true)
    public static boolean isObjectSymbol(char ch) {
        return ch == CHAR_START_OBJECT || ch == CHAR_END_OBJECT;
    }

    @Contract(pure = true)
    public static boolean isArraySymbol(char ch) {
        return ch == CHAR_START_ARRAY || ch == CHAR_END_ARRAY;
    }

    @Contract(pure = true)
    public static boolean isStartSymbol(char ch) {
        return ch == CHAR_START_ARRAY || ch == CHAR_START_OBJECT;
    }

    @Contract(pure = true)
    public static boolean isEndSymbol(char ch) {
        return ch == CHAR_END_ARRAY || ch == CHAR_END_OBJECT;
    }

    @Contract(pure = true)
    public static boolean isObjectSymbol(TYPE type) {
        return type == TYPE.START_OBJECT || type == TYPE.END_OBJECT;
    }

    @Contract(pure = true)
    public static boolean isArraySymbol(TYPE type) {
        return type == TYPE.START_ARRAY || type == TYPE.END_ARRAY;
    }

    @Contract(pure = true)
    public static boolean isStartSymbol(TYPE type) {
        return type == TYPE.START_ARRAY || type == TYPE.START_OBJECT;
    }

    @Contract(pure = true)
    public static boolean isEndSymbol(TYPE type) {
        return type == TYPE.END_ARRAY || type == TYPE.END_OBJECT;
    }

    public static boolean isObjectSymbol(Token token) {
        return isObjectSymbol(token.getType());
    }

    public static boolean isArraySymbol(Token token) {
        return isArraySymbol(token.getType());
    }

    public static boolean isStartSymbol(Token token) {
        return isStartSymbol(token.getType());
    }

    public static boolean isEndSymbol(Token token) {
        return isEndSymbol(token.getType());
    }

    public static boolean isTypeEmbeddedValue(Token token) {
        return isTypeEmbeddedValue(token.getType());
    }

    @Contract(pure = true)
    public static boolean isTypeEmbeddedValue(TYPE type) {
        return type == TYPE.VALUE_JSON_ARRAY || type == TYPE.VALUE_JSON_OBJECT;
    }

}
