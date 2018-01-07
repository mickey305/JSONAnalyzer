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
package com.mickey305.common.v2.json.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TypeTreeFactory {
    private Set<TypeNode<TypeTree>> groupSet;
    private Set<TypeNode<TypeTree>> typeSet;
    private TypeNode<TypeTree>
            root, symbol, key, value,
            start, end, array, object,
            typeEmbedded, bool, number;
    private TypeNode<TypeTree>
            startArray, endArray, startObject, endObject,
            fieldName, valueString, valueNull,
            valueNumberF, valueNumberI, valueNumberDcml,
            valueTrue, valueFalse, valueArray, valueObject;

    public static TypeTreeFactory getInstance() {
        return TypeTreeFactoryHolder.INSTANCE;
    }

    private static class TypeTreeFactoryHolder {
        private static final TypeTreeFactory INSTANCE = new TypeTreeFactory();
    }

    @SuppressWarnings("unchecked")
    private TypeTreeFactory() {
        this.setGroupSet(new HashSet<>());
        this.setTypeSet(new HashSet<>());

        root            = Group.TOKEN_ROOT.box();
        symbol          = Group.SYMBOL.box();
        key             = Group.KEY.box();
        value           = Group.VALUE.box();
        start           = Group.START.box();
        end             = Group.END.box();
        array           = Group.ARRAY.box();
        object          = Group.OBJECT.box();
        typeEmbedded    = Group.TYPE_EMBEDDED.box();
        bool            = Group.BOOLEAN.box();
        number          = Group.NUMBER.box();

        startArray      = Type.START_ARRAY.box();
        endArray        = Type.END_ARRAY.box();
        startObject     = Type.START_OBJECT.box();
        endObject       = Type.END_OBJECT.box();
        fieldName       = Type.FIELD_NAME.box();
        valueString     = Type.VALUE_STRING.box();
        valueNull       = Type.VALUE_NULL.box();
        valueNumberF    = Type.VALUE_NUMBER_F.box();
        valueNumberI    = Type.VALUE_NUMBER_I.box();
        valueNumberDcml = Type.VALUE_NUMBER_DCML.box();
        valueTrue       = Type.VALUE_TRUE.box();
        valueFalse      = Type.VALUE_FALSE.box();
        valueArray      = Type.VALUE_JSON_ARRAY.box();
        valueObject     = Type.VALUE_JSON_OBJECT.box();
    }

    public void build() {
        if(this.getGroupSet().isEmpty()) this.buildGroupTree();
        if(this.getTypeSet().isEmpty()) this.buildTypeTree();
    }

    private void buildGroupTree() {
        // create group structure
        root.add(symbol);
        root.add(key);
        root.add(value);

        symbol.add(start);
        symbol.add(end);
        symbol.add(array);
        symbol.add(object);

        value.add(typeEmbedded);
        value.add(bool);
        value.add(number);

        // store collection groups
        final Set<TypeNode<TypeTree>> groupSet = this.getGroupSet();
        Arrays.stream(Group.values()).map(Group::box).forEach(groupSet::add);

        // change search logic
        groupSet.forEach(group -> group.setCallback((gc, t1) -> gc.getObject() == t1.getObject()));
    }

    private void buildTypeTree() {
        // create type structure
        start.add(startArray);
        start.add(startObject);
        end.add(endArray);
        end.add(endObject);
        array.add(startArray);
        array.add(endArray);
        object.add(startObject);
        object.add(endObject);

        key.add(fieldName);

        value.add(valueString);
        value.add(valueNull);
        typeEmbedded.add(valueObject);
        typeEmbedded.add(valueArray);
        bool.add(valueTrue);
        bool.add(valueFalse);
        number.add(valueNumberDcml);
        number.add(valueNumberF);
        number.add(valueNumberI);

        // store collection types
        final Set<TypeNode<TypeTree>> typeSet = this.getTypeSet();
        Arrays.stream(Type.values()).map(Type::box).forEach(typeSet::add);

        // change search logic
        typeSet.forEach(type -> type.setCallback((tc, t1) -> tc.getObject() == t1.getObject()));
    }

    public Set<TypeNode<TypeTree>> getGroupSet() {
        return groupSet;
    }

    public void setGroupSet(Set<TypeNode<TypeTree>> groupSet) {
        this.groupSet = groupSet;
    }

    public Set<TypeNode<TypeTree>> getTypeSet() {
        return typeSet;
    }

    public void setTypeSet(Set<TypeNode<TypeTree>> typeSet) {
        this.typeSet = typeSet;
    }
}
