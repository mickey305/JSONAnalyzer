/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 - 2018 K.Misaki
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

import com.mickey305.common.v2.json.model.Token;
import com.mickey305.common.v2.json.model.TokenUtil;
import com.mickey305.common.v2.json.model.Type;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.mickey305.common.v2.json.model.Group.KEY;
import static com.mickey305.common.v2.json.model.Group.VALUE;

public interface FinderManager<T extends Token> {

    /**
     * how to search
     */
    enum AccessPoint {
        Key, Value
    }

    /**
     * create searched list
     * @param point target abstract type
     * @param target query
     * @param instance instance
     * @param <I> instance generics: Picker class or subclasses
     * @return result list
     */
    @SuppressWarnings("unchecked")
    default <I extends Picker<T>> List<T> findBy(@NotNull AccessPoint point, String target, @NotNull I instance) {
        List<T> outLst = new ArrayList<>();
        T currentTok;
        int currentDepth;
        for (int i = 1; i < instance.getTokenList().size(); i++) {
            currentTok = instance.getTokenList().get(i);
            currentDepth = currentTok.getDepth();

            T compTok;
            if (point == AccessPoint.Value && TokenUtil.isStartSymbol(finderMethodTargetStartSymbol())
                    && currentTok.getType() == Type.START_OBJECT) {
                compTok = AnalyzeUtil.generateEmbeddedValue(instance.getTokenList(), i, instance.getSupplier());
            } else {
                compTok = currentTok;
            }

            // searching logic
            boolean match = compTok.getString().equals(target);
            if (instance.getOverwriteInterface() != null)
                match = instance.getOverwriteInterface().changeSearchLogic(compTok, target);

            if((point == AccessPoint.Key
                    ? currentTok.getType().belongsTo(KEY)
                    : currentTok.getType().belongsTo(VALUE) || TokenUtil.isStartSymbol(currentTok)) && match) {
                int offset = 1;
                T offsetTok = currentTok;
                while (i - offset >= 0) {
                    offsetTok = instance.getTokenList().get(i - (offset++));
                    if (offsetTok.getDepth() <= currentDepth && TokenUtil.isStartSymbol(offsetTok))
                        break;
                }
                offset--;

                if(offsetTok.getType() == finderMethodTargetStartSymbol())
                    outLst.add(AnalyzeUtil.generateEmbeddedValue(
                            instance.getTokenList(), i - offset, instance.getSupplier()));
            }
        }

        return (outLst);
    }

    /**
     * symbol creator
     * @return start token type of finder method result
     */
    Type finderMethodTargetStartSymbol();
}
