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
import com.mickey305.common.v2.json.model.TokenSupplier;
import com.mickey305.common.v2.json.model.Type;
import com.mickey305.common.v2.json.model.Token;
import com.mickey305.common.v2.json.model.TokenUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

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
 */
public class Picker<T extends Token> implements Cloneable {
    public static final String TAG = Picker.class.getName();

    /**
     * JSON Token List
     */
    private List<T> tokenList;

    private OverwriteInterface overwriteInterface;
    private TokenSupplier<T> supplier;

    /**
     *
     * @param JsonObjectJsonArray
     * @param supplier
     * @throws InsertObjectTypeException
     */
    public Picker(final Object JsonObjectJsonArray, @NotNull TokenSupplier<T> supplier) throws InsertObjectTypeException {
        tokenList = new ArrayList<>();
        this.supplier = supplier;
        this.buildTokenList(JsonObjectJsonArray);
    }

    /**
     *
     * @param inst is JSONObject or JSONArray
     */
    private synchronized void buildTokenList(final Object inst) throws InsertObjectTypeException {
        TokenListBuilder.build(inst, this.tokenList, this.supplier);
    }

    /**
     * clone this Object
     * @return Object
     */
    @Override
    public Picker clone() {
        Picker scope = null;
        try {
            scope = (Picker) super.clone();
            if(tokenList != null) scope.tokenList = new ArrayList<>(this.tokenList);
            if(overwriteInterface != null) scope.overwriteInterface = this.overwriteInterface;
            if(supplier != null) scope.supplier = this.supplier;
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
    protected List<T> createNonKeyValueList(List<T> tokenList) throws JSONSyntaxException {
        List<T> outValueList = new ArrayList<>();
        for(int i = 0; i < tokenList.size(); i++) {
            T token = tokenList.get(i);
            if(i == 0 && token.getType() != Type.START_ARRAY) break;
            if(i == 0) continue;

            if(TokenUtil.isStartSymbol(token))
                outValueList.add(AnalyzeUtil.generateEmbeddedValue(tokenList, i, supplier));

            if(token.getType() == Type.START_OBJECT)
                i = AnalyzeUtil.getPairSymbolPoint(tokenList, i);
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
    public List<T> getAllKeyList() {
        List<T> keyList = new ArrayList<>();
        this.tokenList.stream().filter(
                token -> token.getType() == Type.FIELD_NAME
        ).forEach(keyList::add);
        return (keyList);
    }

    /**
     *
     * @return All Key Hashed List
     */
    public List<T> getAllKeyHashList() {
        List<T> keyHashList = new ArrayList<>();
        Set<String> hashSet = this.getAllKeyHashSet();
        for (String key : hashSet) {
            T token = supplier.get(Type.FIELD_NAME, key);
            keyHashList.add(token);
        }
        return (keyHashList);
    }

    /**
     *
     * @return All Key HashSet
     */
    public Set<String> getAllKeyHashSet() {
        List<T> allKeyList = this.getAllKeyList();
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
    public List<T> getAllValueList() throws JSONSyntaxException {
        // add Value-List of Hash-JSONKey
        List<T> valueList = new ArrayList<>();
        List<T> allKeyHashList = this.getAllKeyHashList();

        while(!allKeyHashList.isEmpty())
            valueList.addAll(this.getValues(allKeyHashList.remove(0).getString()));

        // add Value-List of Not-Contains JSONValue
        List<T> tmpValueList = this.tokenList.stream().filter(
                token -> token.getType().isValue() && !valueList.contains(token)
        ).collect(Collectors.toCollection(ArrayList::new));
        valueList.addAll(tmpValueList);
        List<T> tmpTokenList = new ArrayList<>(this.tokenList);
        tmpValueList = this.createNonKeyValueList(tmpTokenList);
        valueList.addAll(tmpValueList);
        return (valueList);
    }

    /**
     *
     * @return All Value Hashed List
     */
    public List<T> getAllValueHashList() throws JSONSyntaxException {
        List<T> allValueList = this.getAllValueList();
        List<T> valueHashList = new ArrayList<>();
        Set<String> hashSet = this.getAllValueHashSet();
        for (String value : hashSet) {
            Type type = null;
            for (T token : allValueList) {
                if (token.getString().equals(value))
                    type = token.getType();
            }
            assert type != null;
            T token = supplier.get(type, value);
            valueHashList.add(token);
        }
        return (valueHashList);
    }

    /**
     *
     * @return All Value HashSet
     */
    public Set<String> getAllValueHashSet() throws JSONSyntaxException {
        List<T> allValueList = this.getAllValueList();
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
    @SuppressWarnings("unchecked")
    private List<T> getValues(String key, List<T> tokenList) {
        List<T> outList = new ArrayList<>();
        T currentToken;
        for (int i = 0; i < tokenList.size() - 1; i++) {
            currentToken = tokenList.get(i);

            // searching logic
            boolean match = currentToken.getString().equals(key);
            if (overwriteInterface != null)
                match = overwriteInterface.changeSearchLogic(currentToken, key);

            if(currentToken.getType() == Type.FIELD_NAME && match) {
                currentToken = tokenList.get(i + 1);

                if(currentToken.getType().isValue())
                    outList.add(currentToken);
                else if(TokenUtil.isStartSymbol(currentToken))
                    outList.add(AnalyzeUtil.generateEmbeddedValue(tokenList, i + 1, supplier));
            }
        }

        return (outList);
    }

    /**
     *
     * @param key
     * @return
     */
    public List<T> getValues(String key) {
        List<T> tokenList = new ArrayList<>(this.tokenList);
        return this.getValues(key, tokenList);
    }

    /**
     *
     * @param keyList
     * @return
     */
    public List<T> getValues(List<String> keyList) {
        List<T> jsonValueList = this.getValues(keyList.remove(0));
        while (!keyList.isEmpty()) {
            String key = keyList.remove(0);
            List<T> tmpJsonValueList = new ArrayList<>();
            while (!jsonValueList.isEmpty()) {
                final Object obj = jsonValueList.remove(0).getObject();

                // skip others object (only - JSONObject or JSONArray)
                if (!isJSONObjectOrJSONArray(obj))
                    continue;

                List<T> tmpTokenList = new ArrayList<>();
                TokenListBuilder.build(obj, tmpTokenList, this.supplier);
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
    public List<T> getValues(String... keys) {
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
        for(T token: this.tokenList) {
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
    public List<T> searchValues(String key) {
        return this.getValues(key);
    }

    /**
     *
     * @param keyList
     * @return
     */
    public List<T> searchValues(List<String> keyList) {
        return this.getValues(keyList);
    }

    /**
     *
     * @param keys
     * @return
     */
    public List<T> searchValues(String... keys) {
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

    @FunctionalInterface
    public interface OverwriteInterface<T extends Token> {
        boolean changeSearchLogic(T targetToken, String query);
    }

    public void setOverwriteInterface(OverwriteInterface overwriteInterface) {
        this.overwriteInterface = overwriteInterface;
    }

    public OverwriteInterface getOverwriteInterface() {
        return overwriteInterface;
    }

    protected TokenSupplier<T> getSupplier() {
        return supplier;
    }

    protected List<T> getTokenList() {
        return tokenList;
    }
}
