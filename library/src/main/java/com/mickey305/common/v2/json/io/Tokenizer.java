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
package com.mickey305.common.v2.json.io;

import com.mickey305.common.v2.exception.InsertObjectTypeException;
import com.mickey305.common.v2.exception.JSONSyntaxException;
import com.mickey305.common.v2.exception.JSONTokenTypeException;
import com.mickey305.common.v2.json.model.TokenSupplier;
import com.mickey305.common.v2.json.model.TokenUtil;
import com.mickey305.common.v2.json.model.Type;
import com.mickey305.common.v2.json.model.Token;
import com.mickey305.common.v2.json.model.TypeTreeFactory;
import com.mickey305.common.v2.string.ScannerLine;
import com.mickey305.common.v2.util.CollectibleIterator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static com.mickey305.common.v2.json.model.Group.VALUE;

public class Tokenizer<T extends Token> implements CollectibleIterator<T>, Cloneable {
    public static final String TAG = Tokenizer.class.getName();
    public static final int TOKEN_DEPTH_INIT = 0;

    private ScannerLine line;
    private T prevToken;
    private int index;
    private Stack<Boolean> innerJSONArrayStack;
    private IterationCallback iterationCallback;
    private TokenSupplier<T> supplier;

    public Tokenizer(Object t, @NotNull TokenSupplier<T> supplier) throws InsertObjectTypeException {
        this.initialize(t);
        this.supplier = supplier;
    }

    /**
     *
     * @param t
     * @throws InsertObjectTypeException
     */
    private void initialize(Object t) throws InsertObjectTypeException {
        // insert object type check
        IterationUtil.checkObjectType(t);

        // initialize task
        line = new ScannerLine(t.toString());
        prevToken = null;
        index = 0;
        innerJSONArrayStack = new Stack<>();
        // syntax tree build process
        TypeTreeFactory.getInstance().build();
    }

    /**
     *
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public Tokenizer<T> clone() {
        Tokenizer<T> scope = null;
        try {
            scope = (Tokenizer) super.clone();
            if(line != null) scope.line = this.line.clone();
            if(prevToken != null) scope.prevToken = (T) this.prevToken.clone();
            if(innerJSONArrayStack != null) {
                scope.innerJSONArrayStack = new Stack<>();
                scope.innerJSONArrayStack.addAll(this.innerJSONArrayStack);
            }
            if(iterationCallback != null) scope.iterationCallback = this.iterationCallback;
            if(supplier != null) scope.supplier = this.supplier;
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
    public T next() {
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

    private T nextToken() throws JSONSyntaxException, JSONTokenTypeException {
        cutCode(line);

        T tok = null;
        int index = line.getIndex();
        char ch = line.peek();

        if(Character.isDigit(ch) || TokenUtil.isOperator(ch)) {
            // Figure
            tok = this.createFigureToken();
        } else if(Character.isLetter(ch)) {
            // Letter: JSONNullValue or JSONBooleanValue
            tok = this.createEspecialToken();
        } else {
            // Other: Symbol, JSONField or JSONStringValue
            this.chgInnerJSONArrayStack();
            if (TokenUtil.isSymbol(ch)) {
                // JSON symbol
                tok = this.createSymbolToken();
            } else if(ch == TokenUtil.CH_DOUBLE_QUOTES) {
                // JSONField or JSONStringValue
                tok = this.createStandardValueToken();
            } else {
                // error
                if (TokenUtil.isEndSymbol(prevToken) && prevToken.getDepth() == TOKEN_DEPTH_INIT)
                    throw new JSONTokenTypeException("json token end: iteration out of bounds.");
                else
                    throw new JSONSyntaxException("json syntax error: unexpected character type.");
            }
        }

        assert tok != null;
        tok.setIndexNumber(index);
        chgDepth(prevToken, tok);

        // copy JSONToken
        prevToken = tok;

        this.index++;

        return (tok);
    }

    /**
     *
     * @return
     */
    public T now() {
        if(this.prevToken == null)
            throw new NullPointerException("Previous Token Null: please do the next position");

        return this.prevToken;
    }

    /**
     *
     * @return
     */
    public T peek() {
        Tokenizer<T> tokenizer = this.clone();
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
     * @param value
     * @return
     * @throws JSONTokenTypeException
     */
    private T createRegisteredToken(String value) throws JSONTokenTypeException {
        Map<String, Type> map = TokenUtil.getTokenMap();
        if (!map.containsKey(value))
            throw new JSONTokenTypeException("JSONToken type error");
        Type type = map.get(value);
        return supplier.get(type, value);
    }

    /**
     *
     * @return
     */
    private T createFigureToken() {
        T tok;
        String param = "";
        char ch = line.peek();
        while (line.hasNext() && (Character.isDigit(ch) || TokenUtil.isFigureParts(ch))) {
            param += line.next();
            ch = line.peek();
        }
        String expL = String.valueOf(TokenUtil.CH_EXP);
        String expU = String.valueOf(Character.toUpperCase(TokenUtil.CH_EXP));
        if(param.contains(expL) || param.contains(expU)) {
            tok = supplier.get(Type.VALUE_NUMBER_DCML, param);
        } else {
            tok = ((param.contains(String.valueOf(TokenUtil.CH_DOT)))
                    ? supplier.get(Type.VALUE_NUMBER_F, param)
                    : supplier.get(Type.VALUE_NUMBER_I, param));
        }
        return tok;
    }

    /**
     *
     * @return
     * @throws JSONTokenTypeException
     */
    private T createSymbolToken() throws JSONTokenTypeException {
        String str = String.valueOf(line.next());
        return this.createRegisteredToken(str);
    }

    /**
     *
     * @return
     * @throws JSONTokenTypeException
     */
    private T createEspecialToken() throws JSONTokenTypeException {
        String str = "";
        char ch = line.peek();
        while (line.hasNext() && Character.isLetter(ch)) {
            str += line.next();
            ch = line.peek();
        }
        return this.createRegisteredToken(str);
    }

    /**
     *
     * @return
     */
    private T createStandardValueToken() throws JSONSyntaxException {
        Type type;
        Type prevType = prevToken.getType();
        if(!innerJSONArrayStack.peek()) {
            // Index: in JSONObject
            if(prevType == Type.END_ARRAY || TokenUtil.isObjectSymbol(prevToken) || prevType.belongsTo(VALUE))
                type = Type.FIELD_NAME;
            else if(prevType == Type.FIELD_NAME)
                type = Type.VALUE_STRING;
            else
                // json syntax error
                throw new JSONSyntaxException("json syntax error: unexpected value type.");

        } else {
            // Index in JSONArray
            type = Type.VALUE_STRING;
        }
        String param = "";
        line.next(); // skip dq
        char ch = line.peek();
        char prevCh = 0;
        while (line.hasNext() && !(
                ch == TokenUtil.CH_DOUBLE_QUOTES && prevCh != TokenUtil.CH_BACK_SLASH)) {
            param += line.next();
            prevCh = ch;
            ch = line.peek();
        }
        line.next(); // skip dq
        return supplier.get(type, param);
    }

    /**
     *
     */
    private void chgInnerJSONArrayStack() {
        char ch = line.peek();
        switch (ch) {
            case TokenUtil.CH_START_OBJECT:
                innerJSONArrayStack.push(false);
                break;
            case TokenUtil.CH_START_ARRAY:
                innerJSONArrayStack.push(true);
                break;
            case TokenUtil.CH_END_OBJECT:
            case TokenUtil.CH_END_ARRAY:
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

        if(TokenUtil.isStartSymbol(currentToken) && TokenUtil.isEndSymbol(prevToken)) {
            currentToken.setDepth(prevToken.getDepth());
        } else if(TokenUtil.isStartSymbol(currentToken)) {
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
            hasCodeList.add(line.cutCharacter(TokenUtil.CH_COLON));
            hasCodeList.add(line.cutCharacter(TokenUtil.CH_COMMA));
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
