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
package com.mickey305.common.v2.json.model;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenUtil {
    public static final String TAG = TokenUtil.class.getName();

    public static final char CH_DOT = '.';
    public static final char CH_PLUS = '+';
    public static final char CH_MINUS = '-';
    public static final char CH_EXP = 'e';

    public static final char CH_COMMA = ',';
    public static final char CH_COLON = ':';

    public static final char CH_START_ARRAY = '[';
    public static final char CH_END_ARRAY = ']';
    public static final char CH_START_OBJECT = '{';
    public static final char CH_END_OBJECT = '}';
    public static final char CH_DOUBLE_QUOTES = '"';
    public static final char CH_BACK_SLASH = '\\';

    public static final String STRING_NULL  = "null";
    public static final String STRING_TRUE  = "true";
    public static final String STRING_FALSE = "false";

    //===--- Protection Area --------------------------------------------------------------------------------------===//
    private static final Character[] expArray = {
            CH_EXP, Character.toUpperCase(CH_EXP)
    };
    private static final Character[] figurePartsArray = {
            CH_DOT, CH_PLUS, CH_MINUS, CH_EXP, Character.toUpperCase(CH_EXP)
    };
    private static final Character[] operatorArray = {
            CH_PLUS, CH_MINUS
    };
    private static final Character[] symbolArray = {
            CH_START_ARRAY, CH_START_OBJECT, CH_END_ARRAY, CH_END_OBJECT
    };

    private static final List<Character> expList = Arrays.asList(expArray);
    private static final List<Character> figurePartsList = Arrays.asList(figurePartsArray);
    private static final List<Character> operatorList = Arrays.asList(operatorArray);
    private static final List<Character> symbolList = Arrays.asList(symbolArray);

    private TokenUtil() {} // static class protection
    //===----------------------------------------------------------------------------------------------------------===//

    public static Map<String, Type> getTokenMap() {
        Map<String, Type> map = new HashMap<>();
        map.put(String.valueOf(CH_END_ARRAY), Type.END_ARRAY);
        map.put(String.valueOf(CH_END_OBJECT), Type.END_OBJECT);
        map.put(String.valueOf(CH_START_ARRAY), Type.START_ARRAY);
        map.put(String.valueOf(CH_START_OBJECT), Type.START_OBJECT);
        map.put(STRING_FALSE, Type.VALUE_FALSE);
        map.put(STRING_TRUE, Type.VALUE_TRUE);
        map.put(STRING_NULL, Type.VALUE_NULL);
        return map;
    }

    public static Map<Type, Type> getSymbolMap() {
        Map<Type, Type> map = new HashMap<>();
        map.put(Type.START_ARRAY, Type.END_ARRAY);
        map.put(Type.START_OBJECT, Type.END_OBJECT);
        map.put(Type.END_ARRAY, Type.START_ARRAY);
        map.put(Type.END_OBJECT, Type.START_OBJECT);
        return map;
    }

    public static Map<Type, Type> getConversionMap() {
        Map<Type, Type> map = new HashMap<>();
        map.put(Type.START_OBJECT, Type.VALUE_JSON_OBJECT);
        map.put(Type.END_OBJECT, Type.VALUE_JSON_OBJECT);
        map.put(Type.START_ARRAY, Type.VALUE_JSON_ARRAY);
        map.put(Type.END_ARRAY, Type.VALUE_JSON_ARRAY);
        return map;
    }

    /**
     *
     * @param ch
     * @return
     */
    public static boolean isExp(char ch) {
        return expList.contains(ch);
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
        return ch == CH_START_OBJECT || ch == CH_END_OBJECT;
    }

    @Contract(pure = true)
    public static boolean isArraySymbol(char ch) {
        return ch == CH_START_ARRAY || ch == CH_END_ARRAY;
    }

    @Contract(pure = true)
    public static boolean isStartSymbol(char ch) {
        return ch == CH_START_ARRAY || ch == CH_START_OBJECT;
    }

    @Contract(pure = true)
    public static boolean isEndSymbol(char ch) {
        return ch == CH_END_ARRAY || ch == CH_END_OBJECT;
    }

    @Contract(pure = true)
    public static boolean isObjectSymbol(Type type) {
        return type == Type.START_OBJECT || type == Type.END_OBJECT;
    }

    @Contract(pure = true)
    public static boolean isArraySymbol(Type type) {
        return type == Type.START_ARRAY || type == Type.END_ARRAY;
    }

    @Contract(pure = true)
    public static boolean isStartSymbol(Type type) {
        return type == Type.START_ARRAY || type == Type.START_OBJECT;
    }

    @Contract(pure = true)
    public static boolean isEndSymbol(Type type) {
        return type == Type.END_ARRAY || type == Type.END_OBJECT;
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
    public static boolean isTypeEmbeddedValue(Type type) {
        return type == Type.VALUE_JSON_ARRAY || type == Type.VALUE_JSON_OBJECT;
    }

}
