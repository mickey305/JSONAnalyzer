/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 - 2018 K.Misaki
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

public enum Type implements TypeTree {
    START_ARRAY         ( 0),    // [
    END_ARRAY           ( 1),    // ]
    START_OBJECT        ( 2),    // {
    END_OBJECT          ( 3),    // }
    FIELD_NAME          ( 4),    // json key
    VALUE_STRING        ( 5),    // json value
    VALUE_NULL          ( 6),    // json value
    VALUE_NUMBER_F      ( 7),    // json value
    VALUE_NUMBER_I      ( 8),    // json value
    VALUE_NUMBER_DCML   (13),    // json value
    VALUE_TRUE          ( 9),    // json value
    VALUE_FALSE         (10),    // json value
    VALUE_JSON_OBJECT   (11),    // json value
    VALUE_JSON_ARRAY    (12);    // json value

    Type(int code) {
        this.code = code;
        this.cmp = new TypeNode<>(this);
    }

    public int getCode() { return code; }
    public boolean is(Type type) { return this == type; }
    @Override public TypeNode box() { return cmp; }

    private int code;
    private TypeNode<Type> cmp;
}
