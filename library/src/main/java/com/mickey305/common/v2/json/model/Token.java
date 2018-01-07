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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

public class Token implements Cloneable {
    public static final String TAG = Token.class.getName();

    private String string;
    private Type type;
    private int indexNumber;
    private int depth;

    /**
     *
     * @param type
     * @param str
     */
    public Token(Type type, String str) {
        setType(type);
        setString(str);
    }

    /**
     *
     * @return
     */
    @Override
    public Token clone() {
        Token scope = null;
        try {
            scope = (Token) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return scope;
    }

    /**
     *
     * @param object
     * @return
     */
    @Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(this, object);
    }

    /**
     *
     * @return
     */
    public String getString() {
        return string;
    }

    public Object getObject() {
        String str = getString();
        Type type = getType();
        if(type == Type.VALUE_NUMBER_I)
            return Integer.parseInt(str);
        else if(type == Type.VALUE_NUMBER_F)
            return Float.parseFloat(str);
        else if(type == Type.VALUE_JSON_ARRAY)
            return new JSONArray(str);
        else if(type == Type.VALUE_JSON_OBJECT)
            return new JSONObject(str);
        else if(type == Type.VALUE_FALSE || type == Type.VALUE_TRUE)
            return Boolean.parseBoolean(str);
        else
            return str;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     *
     * @param string
     */
    private void setString(String string) {
        this.string = string;
    }

    /**
     * change to {@link #type()}
     * @return
     */
    @Deprecated public Type getType() {
        return this.type();
    }

    /**
     *
     * @return
     */
    public Type type() {
        return type;
    }

    /**
     *
     * @param type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public int getIndexNumber() {
        return indexNumber;
    }

    /**
     *
     * @param indexNumber
     */
    public void setIndexNumber(int indexNumber) {
        this.indexNumber = indexNumber;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
