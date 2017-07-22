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
package com.mickey305.common.v2.json;

import com.mickey305.common.v2.exception.InsertObjectTypeException;
import com.mickey305.common.v2.exception.JSONSyntaxException;
import com.mickey305.common.v2.json.io.TokenListBuilder;
import com.mickey305.common.v2.json.model.Type;
import com.mickey305.common.v2.json.model.Token;
import com.mickey305.common.v2.json.model.TokenUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static com.mickey305.common.v2.json.ObjectTypeChecker.isJSONObjectOrJSONArray;

/**
 * Picker is the Utility Class to search the JSONObject or JSONArray Information.
 *
 * e.g.
 * JSONArray: [{"name":{"first":"hoge","last":"fuga"}},{}]
 * ---------------------------------------------------
 * JSONKey is name, first and last
 * JSONValue is {"first":"hoge","last":"fuga"}, hoge, fuga, {} and {"name":{"first":"hoge","last":"fuga"}}
 * JSONTokenSymbol is {, }, [ and ]
 *
 * @param <T> generics: JSONObject or JSONArray
 */
public class Picker<T> implements Cloneable {
    public static final String TAG = Picker.class.getName();

    /**
     * JSON Token List
     */
    private List<Token> tokenList;

    private OverwriteInterface overwriteInterface;

    /**
     *
     * @param JsonObjectJsonArray
     */
    public Picker(final T JsonObjectJsonArray) throws InsertObjectTypeException {
        tokenList = new ArrayList<>();
        this.buildTokenList(JsonObjectJsonArray);
    }

    /**
     *
     * @param inst is JSONObject or JSONArray
     */
    private synchronized void buildTokenList(final T inst) throws InsertObjectTypeException {
        TokenListBuilder.build(inst, this.tokenList);
    }

    /**
     * clone this Object
     * @return Object
     */
    @Override
    @SuppressWarnings("unchecked")
    public Picker<T> clone() {
        Picker<T> scope = null;
        try {
            scope = (Picker<T>) super.clone();
            if(tokenList != null) scope.tokenList = new ArrayList<>(this.tokenList);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return (scope);
    }

    /**
     * Not-Pair Value List creator
     * @param tokenList is JSON Token List
     * @return Value Token List
     */
    protected List<Token> createNonKeyValueList(List<Token> tokenList) throws JSONSyntaxException {
        List<Token> outValueList = new ArrayList<>();
        for(int i = 0; i < tokenList.size(); i++) {
            Token token = tokenList.get(i);
            if(i == 0 && token.getType() != Type.START_ARRAY) break;
            if(i == 0) continue;

            if(TokenUtil.isStartSymbol(token))
                outValueList.add(this.generateEmbeddedValue(tokenList, i));

            if(token.getType() == Type.START_OBJECT)
                i = getPairSymbolPoint(tokenList, i);
        }
        return (outValueList);
    }

    /**
     *
     * @param object
     * @return
     */
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(this, object);
    }

    /**
     *
     * @return All Key List contained in JSONObject or JSONArray
     */
    public List<Token> getAllKeyList() {
        List<Token> keyList = new ArrayList<>();
        this.tokenList.stream().filter(
                token -> token.getType() == Type.FIELD_NAME
        ).forEach(keyList::add);
        return (keyList);
    }

    /**
     *
     * @return All Key Hashed List
     */
    public List<Token> getAllKeyHashList() {
        List<Token> keyHashList = new ArrayList<>();
        Set<String> hashSet = this.getAllKeyHashSet();
        for (String aHashSet : hashSet) {
            Token token = new Token(Type.FIELD_NAME, aHashSet);
            keyHashList.add(token);
        }
        return (keyHashList);
    }

    /**
     *
     * @return All Key HashSet
     */
    public Set<String> getAllKeyHashSet() {
        List<Token> allKeyList = this.getAllKeyList();
        List<String> strAllKeyList = new ArrayList<>();

        while (!allKeyList.isEmpty())
            strAllKeyList.add(allKeyList.remove(0).getString());

        HashSet<String> allKeyHashSet = new HashSet<>();
        allKeyHashSet.addAll(strAllKeyList);
        return (allKeyHashSet);
    }

    /**
     *
     * @return All Value List contained in JSONObject or JSONArray
     */
    public List<Token> getAllValueList() throws JSONSyntaxException {
        // add Value-List of Hash-JSONKey
        List<Token> valueList = new ArrayList<>();
        List<Token> allKeyHashList = this.getAllKeyHashList();

        while(!allKeyHashList.isEmpty())
            valueList.addAll(this.getValues(allKeyHashList.remove(0).getString()));

        // add Value-List of Not-Contains JSONValue
        List<Token> tmpValueList = this.tokenList.stream().filter(
                token -> token.getType().isValue() && !valueList.contains(token)
        ).collect(Collectors.toCollection(ArrayList::new));
        valueList.addAll(tmpValueList);
        List<Token> tmpTokenList = new ArrayList<>(this.tokenList);
        tmpValueList = this.createNonKeyValueList(tmpTokenList);
        valueList.addAll(tmpValueList);
        return (valueList);
    }

    /**
     *
     * @return All Value Hashed List
     */
    public List<Token> getAllValueHashList() throws JSONSyntaxException {
        List<Token> allValueList = this.getAllValueList();
        List<Token> valueHashList = new ArrayList<>();
        Set<String> hashSet = this.getAllValueHashSet();
        for (String value : hashSet) {
            Type type = null;
            for (Token token : allValueList) {
                if (token.getString().equals(value))
                    type = token.getType();
            }
            assert type != null;
            Token token = new Token(type, value);
            valueHashList.add(token);
        }
        return (valueHashList);
    }

    /**
     *
     * @return All Value HashSet
     */
    public Set<String> getAllValueHashSet() throws JSONSyntaxException {
        List<Token> allValueList = this.getAllValueList();
        List<String> strAllValueList = new ArrayList<>();
        while (!allValueList.isEmpty())
            strAllValueList.add(allValueList.remove(0).getString());
        HashSet<String> allValueHashSet = new HashSet<>();
        allValueHashSet.addAll(strAllValueList);
        return (allValueHashSet);
    }

    /*
     * The Tutorial of getValues and searchValues method
     * e.g.
     * registered json ----------------------------------------------------
     * [{
     *  "name" : { "first" : "taro", "last" : "suzuki" },
     *  "mail" : "taro@example.jp",
     *  "todoList" : { "work" : "report", "limit" : "1994/05/13" }
     * },
     * {
     *  "name" : { "first" : "satoshi", "last" : "maeda" },
     *  "mail" : "satoshi0612@example.jp",
     *  "todoList" : { "work" : "test", "limit" : "2015/06/12" }
     * },
     * {
     *  "name" : { "first" : "hanako", "last" : "tanaka" },
     *  "mail" : "hanako_teacher@example.jp",
     *  "birthday" : "1985/02/13",
     *  "todoList" : { "work" : "book the hotel", "limit" : "2005/03/31" }
     * }]
     *
     * called method ------------------------------------------------------
     * input -->
     *  keyList.add("name");
     *  keyList.add("last");
     *  OR
     *  method signature : "name", "last"
     *  OR
     *  keyList.add("last");
     * output -->
     *  suzuki
     *  maeda
     *  tanaka as LinkedList<JSONToken>
     *
     * input -->
     *  keyList.add("last");
     *  keyList.add("name");
     *  OR
     *  method signature : "last", "name"
     * output -->
     *  LinkedList<JSONToken> is Empty(Exception Occurred)
     *
     * input -->
     *  keyList.add("birthday");
     * output -->
     *  1985/02/13 as LinkedList<JSONToken>
     *
     * input -->
     *  keyList.add("name");
     * output -->
     *  { "first" : "taro", "last" : "suzuki" }
     *  { "first" : "satoshi", "last" : "maeda" }
     *  { "first" : "hanako", "last" : "tanaka" } as LinkedList<JSONToken>
     */

    /**
     *
     * @param key is pair of JSONValue to search
     * @param tokenList JSON Token List
     * @return Matched JSONValue List
     */
    private List<Token> getValues(String key, List<Token> tokenList) {
        List<Token> outList = new ArrayList<>();
        Token currentToken;
        for (int i = 0; i < tokenList.size() - 1; i++) {
            currentToken = tokenList.get(i);

            // searching logic
            boolean match = currentToken.getString().equals(key);
            if (overwriteInterface != null)
                match = overwriteInterface.changeSearchLogic(currentToken.getString(), key);

            if(currentToken.getType() == Type.FIELD_NAME && match) {
                currentToken = tokenList.get(i + 1);

                if(currentToken.getType().isValue())
                    outList.add(currentToken);
                else if(TokenUtil.isStartSymbol(currentToken))
                    outList.add(this.generateEmbeddedValue(tokenList, i + 1));
            }
        }

        return (outList);
    }

    /**
     *
     * @param key
     * @return
     */
    public List<Token> getValues(String key) {
        List<Token> tokenList = new ArrayList<>(this.tokenList);
        return this.getValues(key, tokenList);
    }

    /**
     *
     * @param keyList
     * @return
     */
    public List<Token> getValues(List<String> keyList) {
        List<Token> jsonValueList = this.getValues(keyList.remove(0));
        while (!keyList.isEmpty()) {
            String key = keyList.remove(0);
            List<Token> tmpJsonValueList = new ArrayList<>();
            while (!jsonValueList.isEmpty()) {
                final Object obj = jsonValueList.remove(0).getObject();

                // skip others object (only - JSONObject or JSONArray)
                if (!isJSONObjectOrJSONArray(obj))
                    continue;

                List<Token> tmpTokenList = new ArrayList<>();
                TokenListBuilder.build(obj, tmpTokenList);
                tmpJsonValueList.addAll(this.getValues(key, tmpTokenList));
            }
            jsonValueList = tmpJsonValueList;
        }
        return (jsonValueList);
    }

    /**
     *
     * @param keys
     * @return
     */
    public List<Token> getValues(String... keys) {
        List<String> keyList = Arrays.asList(keys);
        return this.getValues(new ArrayList<>(keyList));
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
     * @param key
     * @return
     */
    public boolean isExistKey(String key) {
        for(Token token: this.tokenList) {
            if(token.getType() == Type.FIELD_NAME && token.getString().equals(key))
                return true;
        }
        return false;
    }

    /**
     *
     * @param keys
     * @return
     */
    public boolean isExistAllKeys(List<String> keys) {
        final int size = keys.size();
        int count = 0;
        for(String key: keys) {
            if(this.isExistKey(key)) count++;
        }
        return size == count;
    }

    /**
     *
     * @param keys
     * @return
     */
    public boolean isExistAllKeys(String... keys) {
        List<String> keyList = new ArrayList<>();
        Collections.addAll(keyList, keys);
        return this.isExistAllKeys(keyList);
    }

    /**
     * the same function with getValues
     * @param key
     * @return
     */
    public List<Token> searchValues(String key) {
        return this.getValues(key);
    }

    /**
     *
     * @param keyList
     * @return
     */
    public List<Token> searchValues(List<String> keyList) {
        return this.getValues(keyList);
    }

    /**
     *
     * @param keys
     * @return
     */
    public List<Token> searchValues(String... keys) {
        return this.getValues(keys);
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Create Embedded-JSONValue(JSONObject or JSONArray)
     * @param tokenList is JSON Token List
     * @param start is START Embedded-Symbol Index
     * @return Embedded JSONToken(Embedded Value)
     */
    protected Token generateEmbeddedValue(List<Token> tokenList, int start) {
        Token currentToken;
        Token nextToken;
        String value = "";
        final Token startToken = tokenList.get(start);
        final Type startType = startToken.getType();
        final Type endType = TokenUtil.getSymbolMap().get(startType);

        assert TokenUtil.isStartSymbol(startType);
        assert TokenUtil.isEndSymbol(endType);
        for (int k = start; k < tokenList.size() - 1; k++) {
            currentToken = tokenList.get(k);
            nextToken = tokenList.get(k + 1);
            boolean isEnd = currentToken.getType() == endType && startToken.getDepth() == currentToken.getDepth();

            // create token-string and added double quotes
            if(currentToken.getType() == Type.VALUE_STRING || currentToken.getType() == Type.FIELD_NAME)
                value += TokenUtil.CHAR_DOUBLE_QUOTES + currentToken.getString() + TokenUtil.CHAR_DOUBLE_QUOTES;
            else
                value += currentToken.getString();

            // added colon
            if(currentToken.getType() == Type.FIELD_NAME)
                value += TokenUtil.CHAR_COLON;

            // added comma
            if(currentToken.getType().isValue() || TokenUtil.isEndSymbol(currentToken)) {
                if(!TokenUtil.isEndSymbol(nextToken) && !isEnd)
                    value += TokenUtil.CHAR_COMMA;
            }

            if(isEnd) break;
        }
        return new Token(TokenUtil.getConversionMap().get(startType), value);
    }

    /**
     *
     * @param tokens
     * @param start
     * @return
     * @throws JSONSyntaxException
     */
    private int getPairSymbolPoint(List<Token> tokens, int start) throws JSONSyntaxException {
        final Token startToken = tokens.get(start);
        final Type endType = TokenUtil.getSymbolMap().get(startToken.getType());
        for (int index = start; index < tokens.size(); index++) {
            Token currentToken = tokens.get(index);

            if (currentToken.getType() == endType && startToken.getDepth() == currentToken.getDepth())
                return index;
        }
        throw new JSONSyntaxException("JSON format error: pleanse check insert token data.");
    }

    public interface OverwriteInterface {
        boolean changeSearchLogic(String targetKey, String queryKey);
    }

    public void setOverwriteInterface(OverwriteInterface overwriteInterface) {
        this.overwriteInterface = overwriteInterface;
    }

}
