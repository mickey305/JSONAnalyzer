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
import com.mickey305.common.v2.exception.JSONSyntaxException;
import com.mickey305.common.v2.exception.JSONTokenTypeException;
import com.mickey305.common.v2.json.model.TokenUtil;
import com.mickey305.common.v2.json.model.TYPE;
import com.mickey305.common.v2.json.model.Token;
import com.mickey305.common.v2.string.ScannerLine;
import com.mickey305.common.v2.util.CollectibleIterator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Tokenizer<T> implements CollectibleIterator<Token>, Cloneable {
    public static final String TAG = Tokenizer.class.getName();
    public static final int TOKEN_DEPTH_INIT = 0;

    private ScannerLine line;
    private Token prevToken;
    private int index;
    private Stack<Boolean> innerJSONArrayStack;
    private IterationCallback iterationCallback;

    public Tokenizer(T t) throws InsertObjectTypeException {
        this.initialize(t);
    }

    /**
     *
     * @param t
     * @throws InsertObjectTypeException
     */
    private void initialize(T t) throws InsertObjectTypeException {
        // insert object type check
        IterationUtil.checkObjectType(t);

        // initialize task
        line = new ScannerLine(t.toString());
        prevToken = null;
        index = 0;
        innerJSONArrayStack = new Stack<>();
    }

    /**
     *
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public Tokenizer clone() {
        Tokenizer scope = null;
        try {
            scope = (Tokenizer) super.clone();
            if(line != null) scope.line = this.line.clone();
            if(prevToken != null) scope.prevToken = this.prevToken.clone();
            if(innerJSONArrayStack != null) {
                scope.innerJSONArrayStack = new Stack<>();
                scope.innerJSONArrayStack.addAll(this.innerJSONArrayStack);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return scope;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasNext() {
        ScannerLine line = this.line.clone();
        cutCode(line);
        return line.hasNext();
    }

    /**
     *
     * @return
     */
    @Override
    public Token next() {
        try {
            return this.nextToken();
        } catch (JSONSyntaxException e) {
            e.printStackTrace();
            if (iterationCallback != null)
                this.iterationCallback.onStop();
        } catch (JSONTokenTypeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Token nextToken() throws JSONSyntaxException, JSONTokenTypeException {
        cutCode(line);

        Token token = null;
        int index = line.getIndex();
        char ch = line.peek();

        if(Character.isDigit(ch) || TokenUtil.isOperator(ch)) {
            // Figure
            token = this.createFigureToken();
        } else if(Character.isLetter(ch)) {
            // Letter: JSONNullValue or JSONBooleanValue
            token = this.createEspecialToken();
        } else {
            // Other: Symbol, JSONField or JSONStringValue
            this.chgInnerJSONArrayStack();
            if (TokenUtil.isSymbol(ch)) {
                // JSON symbol
                token = new Token(line.next());
            } else if(ch == TokenUtil.CHAR_DOUBLE_QUOTES) {
                // JSONField or JSONStringValue
                token = this.createStandardValueToken();
            } else {
                // error
                if (TokenUtil.isEndSymbol(prevToken) && prevToken.getDepth() == TOKEN_DEPTH_INIT)
                    throw new JSONTokenTypeException("json token end: iteration out of bounds.");
                else
                    throw new JSONSyntaxException("json syntax error: unexpected character type.");
            }
        }

        assert token != null;
        token.setIndexNumber(index);
        chgDepth(prevToken, token);

        // copy JSONToken
        prevToken = token;

        this.index++;

        return (token);
    }

    /**
     *
     * @return
     */
    public Token now() {
        if(this.prevToken == null)
            throw new NullPointerException("Previous Token Null: please do the next position");

        return this.prevToken;
    }

    /**
     *
     * @return
     */
    public Token peek() {
        Tokenizer tokenizer = this.clone();
        return tokenizer.next();
    }

    /**
     *
     * @param loop
     */
    public void skip(int loop) {
        if (loop < 0) loop = 0;

        for (int i = 0; i < loop; i++)
            this.next();
    }

    public int getIndex() {
        return index;
    }

    /**
     *
     * @return
     */
    private Token createFigureToken() {
        Token token;
        String param = "";
        char ch = line.peek();
        while (line.hasNext() && (Character.isDigit(ch) || TokenUtil.isFigureParts(ch))) {
            param += line.next();
            ch = line.peek();
        }
        token = (param.contains(String.valueOf(TokenUtil.CHAR_DOT)))?
                new Token(Float.parseFloat(param)):
                new Token(Integer.parseInt(param));
        return token;
    }

    /**
     *
     * @return
     * @throws JSONTokenTypeException
     */
    private Token createEspecialToken() throws JSONTokenTypeException {
        String str = "";
        char ch = line.peek();
        while (line.hasNext() && Character.isLetter(ch)) {
            str += line.next();
            ch = line.peek();
        }
        return new Token(str);
    }

    /**
     *
     * @return
     */
    private Token createStandardValueToken() throws JSONSyntaxException {
        TYPE type;
        TYPE prevType = prevToken.getType();
        if(!innerJSONArrayStack.peek()) {
            // Index: in JSONObject
            if(prevType == TYPE.END_ARRAY || TokenUtil.isObjectSymbol(prevToken) || prevType.isValue())
                type = TYPE.FIELD_NAME;
            else if(prevType == TYPE.FIELD_NAME)
                type = TYPE.VALUE_STRING;
            else
                // json syntax error
                throw new JSONSyntaxException("json syntax error: unexpected value type.");

        } else {
            // Index in JSONArray
            type = TYPE.VALUE_STRING;
        }
        String param = "";
        line.next();
        char ch = line.peek();
        while (line.hasNext() && ch != TokenUtil.CHAR_DOUBLE_QUOTES) {
            param += line.next();
            ch = line.peek();
        }
        line.next();
        return new Token(type, param);
    }

    /**
     *
     */
    private void chgInnerJSONArrayStack() {
        char ch = line.peek();
        switch (ch) {
            case TokenUtil.CHAR_START_OBJECT:
                innerJSONArrayStack.push(false);
                break;
            case TokenUtil.CHAR_START_ARRAY:
                innerJSONArrayStack.push(true);
                break;
            case TokenUtil.CHAR_END_OBJECT:
            case TokenUtil.CHAR_END_ARRAY:
                innerJSONArrayStack.pop();
                break;
            default:
                break;
        }
    }

    private static void chgDepth(Token prevToken, Token currentToken) {
        if(prevToken == null) {
            currentToken.setDepth(TOKEN_DEPTH_INIT);
            return;
        }

        if(TokenUtil.isStartSymbol(currentToken)) {
            currentToken.setDepth(prevToken.getDepth() + 1);
        } else if(TokenUtil.isEndSymbol(prevToken)) {
            currentToken.setDepth(prevToken.getDepth() - 1);
        } else {
            currentToken.setDepth(prevToken.getDepth());
        }
    }

    /**
     *
     */
    private static void cutCode(ScannerLine line) {
        List<Boolean> hasCodeList = new ArrayList<>();
        do {
            hasCodeList.clear();
            hasCodeList.add(line.cutWhitespace());
            hasCodeList.add(line.cutCharacter(TokenUtil.CHAR_COLON));
            hasCodeList.add(line.cutCharacter(TokenUtil.CHAR_COMMA));
        } while (hasCodeList.contains(true));
    }

    public void setIterationCallback(IterationCallback iterationCallback) {
        this.iterationCallback = iterationCallback;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(this, object);
    }
}
