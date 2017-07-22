/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 K.Misaki
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

import com.mickey305.common.v2.exception.JSONSyntaxException;
import com.mickey305.common.v2.json.model.Token;
import com.mickey305.common.v2.json.model.TokenSupplier;
import com.mickey305.common.v2.json.model.TokenUtil;
import com.mickey305.common.v2.json.model.Type;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnalyzeUtil {

    /**
     * Create Embedded-JSONValue(JSONObject or JSONArray)
     * @param tokenList is JSON Token List
     * @param start is START Embedded-Symbol Index
     * @param supplier is token instance creator(notnull)
     * @param <T> generic type: Token class or subclasses
     * @return Embedded JSONToken(Embedded Value)
     */
    static <T extends Token> T generateEmbeddedValue(
            List<T> tokenList, int start, @NotNull TokenSupplier<T> supplier) {
        T currentTok;
        T nextTok;
        String val = "";
        final T startToken = tokenList.get(start);
        final Type startType = startToken.getType();
        final Type endType = TokenUtil.getSymbolMap().get(startType);

        assert TokenUtil.isStartSymbol(startType);
        assert TokenUtil.isEndSymbol(endType);
        for (int k = start; k < tokenList.size() - 1; k++) {
            currentTok = tokenList.get(k);
            nextTok = tokenList.get(k + 1);
            boolean isEnd = currentTok.getType() == endType && startToken.getDepth() == currentTok.getDepth();

            // create token-string and added double quotes
            if(currentTok.getType() == Type.VALUE_STRING || currentTok.getType() == Type.FIELD_NAME)
                val += TokenUtil.CH_DOUBLE_QUOTES + currentTok.getString() + TokenUtil.CH_DOUBLE_QUOTES;
            else
                val += currentTok.getString();

            // added colon
            if(currentTok.getType() == Type.FIELD_NAME)
                val += TokenUtil.CH_COLON;

            // added comma
            if(currentTok.getType().isValue() || TokenUtil.isEndSymbol(currentTok)) {
                if(!TokenUtil.isEndSymbol(nextTok) && !isEnd)
                    val += TokenUtil.CH_COMMA;
            }

            if(isEnd) break;
        }
        T outTok = supplier.get(TokenUtil.getConversionMap().get(startType), val);
        outTok.setIndexNumber(startToken.getIndexNumber());
        outTok.setDepth(startToken.getIndexNumber());
        return outTok;
    }

    /**
     *
     * @param tokens
     * @param start
     * @return
     * @throws JSONSyntaxException
     */
    static <T extends Token> int getPairSymbolPoint(List<T> tokens, int start) throws JSONSyntaxException {
        final T startTok = tokens.get(start);
        final Type endType = TokenUtil.getSymbolMap().get(startTok.getType());
        for (int index = start; index < tokens.size(); index++) {
            T currentToken = tokens.get(index);

            if (currentToken.getType() == endType && startTok.getDepth() == currentToken.getDepth())
                return index;
        }
        throw new JSONSyntaxException("JSON format error: please check insert token data.");
    }
}
