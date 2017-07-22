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

public enum Type {
    START_ARRAY         ( 0, true,  false),    // [
    END_ARRAY           ( 1, true,  false),    // ]
    START_OBJECT        ( 2, true,  false),    // {
    END_OBJECT          ( 3, true,  false),    // }
    FIELD_NAME          ( 4, false, false),    // json key
    VALUE_STRING        ( 5, false, true ),    // json value
    VALUE_NULL          ( 6, false, true ),    // json value
    VALUE_NUMBER_F      ( 7, false, true ),    // json value
    VALUE_NUMBER_I      ( 8, false, true ),    // json value
    VALUE_TRUE          ( 9, false, true ),    // json value
    VALUE_FALSE         (10, false, true ),    // json value
    VALUE_JSON_OBJECT   (11, false, true ),    // json value
    VALUE_JSON_ARRAY    (12, false, true );    // json value

    Type(int code, boolean symbol, boolean value) {
        this.code = code;
        this.symbol = symbol;
        this.value = value;
    }

    public int getCode() { return code; }
    public boolean isSymbol() { return symbol; }
    public boolean isValue() { return value; }
    public boolean isKey() { return !isSymbol() && !isValue(); }

    private int code;
    private boolean symbol;
    private boolean value;
}
