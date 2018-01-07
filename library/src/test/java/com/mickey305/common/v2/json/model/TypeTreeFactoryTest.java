package com.mickey305.common.v2.json.model;

import com.mickey305.foundation.v3.util.Log;
import com.mickey305.foundation.v3.util.SetUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mickey305.common.v2.json.model.Group.BOOLEAN;
import static com.mickey305.common.v2.json.model.Group.END;
import static com.mickey305.common.v2.json.model.Group.KEY;
import static com.mickey305.common.v2.json.model.Group.NUMBER;
import static com.mickey305.common.v2.json.model.Group.OBJECT;
import static com.mickey305.common.v2.json.model.Group.START;
import static com.mickey305.common.v2.json.model.Group.SYMBOL;
import static com.mickey305.common.v2.json.model.Group.ARRAY;
import static com.mickey305.common.v2.json.model.Group.TOKEN_ROOT;
import static com.mickey305.common.v2.json.model.Group.TYPE_EMBEDDED;
import static com.mickey305.common.v2.json.model.Group.VALUE;
import static com.mickey305.common.v2.json.model.Type.END_ARRAY;
import static com.mickey305.common.v2.json.model.Type.END_OBJECT;
import static com.mickey305.common.v2.json.model.Type.FIELD_NAME;
import static com.mickey305.common.v2.json.model.Type.START_ARRAY;
import static com.mickey305.common.v2.json.model.Type.START_OBJECT;
import static com.mickey305.common.v2.json.model.Type.VALUE_FALSE;
import static com.mickey305.common.v2.json.model.Type.VALUE_JSON_ARRAY;
import static com.mickey305.common.v2.json.model.Type.VALUE_JSON_OBJECT;
import static com.mickey305.common.v2.json.model.Type.VALUE_NULL;
import static com.mickey305.common.v2.json.model.Type.VALUE_NUMBER_DCML;
import static com.mickey305.common.v2.json.model.Type.VALUE_NUMBER_F;
import static com.mickey305.common.v2.json.model.Type.VALUE_NUMBER_I;
import static com.mickey305.common.v2.json.model.Type.VALUE_STRING;
import static com.mickey305.common.v2.json.model.Type.VALUE_TRUE;

public class TypeTreeFactoryTest {
    private Set<Type> types;
    private Set<Group> groups;
    private Set<TypeTree> allSet;

    @Before
    public void setUp() throws Exception {
        types = SetUtil.fromArray(Type.values());
        groups = SetUtil.fromArray(Group.values());
        allSet = new HashSet<>();
        allSet.addAll(types);
        allSet.addAll(groups);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void build() throws Exception {
        TypeTreeFactory tree = TypeTreeFactory.getInstance();
        tree.build();
        List<Pair<List<TypeTree>, Function<TypeTree, Boolean>>> testArguments = new ArrayList<>();

        // from "token-root" to any tree-path test pattern
        testArguments.add(Pair.of(
                allSet.stream().filter(elm -> !elm.is(TOKEN_ROOT)).collect(Collectors.toList()),
                TOKEN_ROOT::contains));
        testArguments.add(Pair.of(
                Collections.emptyList(),
                TOKEN_ROOT::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(TOKEN_ROOT),
                TOKEN_ROOT::is));
        testArguments.add(Pair.of(
                new ArrayList<>(allSet),
                TOKEN_ROOT::directLine));
        // from "symbol" to any tree-path test pattern
        testArguments.add(Pair.of(
                Arrays.asList(START, END, ARRAY, OBJECT, START_ARRAY, START_OBJECT, END_ARRAY, END_OBJECT),
                SYMBOL::contains));
        testArguments.add(Pair.of(
                Collections.singletonList(TOKEN_ROOT),
                SYMBOL::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(SYMBOL),
                SYMBOL::is));
        testArguments.add(Pair.of(
                Arrays.asList(START, END, ARRAY, OBJECT, START_ARRAY, START_OBJECT, END_ARRAY, END_OBJECT,
                        TOKEN_ROOT, SYMBOL),
                SYMBOL::directLine));
        // from "key" to any tree-path test pattern
        testArguments.add(Pair.of(
                Collections.singletonList(FIELD_NAME),
                KEY::contains));
        testArguments.add(Pair.of(
                Collections.singletonList(TOKEN_ROOT),
                KEY::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(KEY),
                KEY::is));
        testArguments.add(Pair.of(
                Arrays.asList(FIELD_NAME, TOKEN_ROOT, KEY),
                KEY::directLine));
        // from "value" to any tree-path test pattern
        testArguments.add(Pair.of(
                Arrays.asList(TYPE_EMBEDDED, BOOLEAN, NUMBER, VALUE_STRING, VALUE_JSON_ARRAY, VALUE_JSON_OBJECT,
                        VALUE_NUMBER_F, VALUE_NUMBER_I, VALUE_FALSE, VALUE_NULL, VALUE_TRUE, VALUE_NUMBER_DCML),
                VALUE::contains));
        testArguments.add(Pair.of(
                Collections.singletonList(TOKEN_ROOT),
                VALUE::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(VALUE),
                VALUE::is));
        testArguments.add(Pair.of(
                Arrays.asList(TYPE_EMBEDDED, BOOLEAN, NUMBER, VALUE_STRING, VALUE_JSON_ARRAY, VALUE_JSON_OBJECT,
                        VALUE_NUMBER_F, VALUE_NUMBER_I, VALUE_FALSE, VALUE_NULL, VALUE_TRUE, VALUE_NUMBER_DCML,
                        TOKEN_ROOT, VALUE),
                VALUE::directLine));
        // from "start" to any tree-path test pattern
        testArguments.add(Pair.of(
                Arrays.asList(START_ARRAY, START_OBJECT),
                START::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, SYMBOL),
                START::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(START),
                START::is));
        testArguments.add(Pair.of(
                Arrays.asList(START_ARRAY, START_OBJECT, TOKEN_ROOT, SYMBOL, START),
                START::directLine));
        // from "end" to any tree-path test pattern
        testArguments.add(Pair.of(
                Arrays.asList(END_ARRAY, END_OBJECT),
                END::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, SYMBOL),
                END::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(END),
                END::is));
        testArguments.add(Pair.of(
                Arrays.asList(END_ARRAY, END_OBJECT, TOKEN_ROOT, SYMBOL, END),
                END::directLine));
        // from "array" to any tree-path test pattern
        testArguments.add(Pair.of(
                Arrays.asList(START_ARRAY, END_ARRAY),
                ARRAY::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, SYMBOL),
                ARRAY::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(ARRAY),
                ARRAY::is));
        testArguments.add(Pair.of(
                Arrays.asList(START_ARRAY, END_ARRAY, TOKEN_ROOT, SYMBOL, ARRAY),
                ARRAY::directLine));
        // from "object" to any tree-path test pattern
        testArguments.add(Pair.of(
                Arrays.asList(START_OBJECT, END_OBJECT),
                OBJECT::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, SYMBOL),
                OBJECT::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(OBJECT),
                OBJECT::is));
        testArguments.add(Pair.of(
                Arrays.asList(START_OBJECT, END_OBJECT, TOKEN_ROOT, SYMBOL, OBJECT),
                OBJECT::directLine));
        // from "type-embedded" to any tree-path test pattern
        testArguments.add(Pair.of(
                Arrays.asList(VALUE_JSON_ARRAY, VALUE_JSON_OBJECT),
                TYPE_EMBEDDED::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE),
                TYPE_EMBEDDED::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(TYPE_EMBEDDED),
                TYPE_EMBEDDED::is));
        testArguments.add(Pair.of(
                Arrays.asList(VALUE_JSON_ARRAY, VALUE_JSON_OBJECT, TOKEN_ROOT, VALUE, TYPE_EMBEDDED),
                TYPE_EMBEDDED::directLine));
        // from "boolean" to any tree-path test pattern
        testArguments.add(Pair.of(
                Arrays.asList(VALUE_TRUE, VALUE_FALSE),
                BOOLEAN::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE),
                BOOLEAN::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(BOOLEAN),
                BOOLEAN::is));
        testArguments.add(Pair.of(
                Arrays.asList(VALUE_TRUE, VALUE_FALSE, TOKEN_ROOT, VALUE, BOOLEAN),
                BOOLEAN::directLine));
        // from "number" to any tree-path test pattern
        testArguments.add(Pair.of(
                Arrays.asList(VALUE_NUMBER_F, VALUE_NUMBER_I, VALUE_NUMBER_DCML),
                NUMBER::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE),
                NUMBER::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(NUMBER),
                NUMBER::is));
        testArguments.add(Pair.of(
                Arrays.asList(VALUE_NUMBER_F, VALUE_NUMBER_I, VALUE_NUMBER_DCML, TOKEN_ROOT, VALUE, NUMBER),
                NUMBER::directLine));

        // from "start-array" to any tree-path test pattern
        testArguments.add(Pair.of(
                Collections.emptyList(),
                START_ARRAY::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, SYMBOL, START, ARRAY),
                START_ARRAY::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(START_ARRAY),
                START_ARRAY::is));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, SYMBOL, START, ARRAY, START_ARRAY),
                START_ARRAY::directLine));
        // from "end-array" to any tree-path test pattern
        testArguments.add(Pair.of(
                Collections.emptyList(),
                END_ARRAY::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, SYMBOL, END, ARRAY),
                END_ARRAY::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(END_ARRAY),
                END_ARRAY::is));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, SYMBOL, END, ARRAY, END_ARRAY),
                END_ARRAY::directLine));
        // from "start-object" to any tree-path test pattern
        testArguments.add(Pair.of(
                Collections.emptyList(),
                START_OBJECT::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, SYMBOL, START, OBJECT),
                START_OBJECT::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(START_OBJECT),
                START_OBJECT::is));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, SYMBOL, START, OBJECT, START_OBJECT),
                START_OBJECT::directLine));
        // from "end-object" to any tree-path test pattern
        testArguments.add(Pair.of(
                Collections.emptyList(),
                END_OBJECT::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, SYMBOL, END, OBJECT),
                END_OBJECT::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(END_OBJECT),
                END_OBJECT::is));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, SYMBOL, END, OBJECT, END_OBJECT),
                END_OBJECT::directLine));
        // from "field-name" to any tree-path test pattern
        testArguments.add(Pair.of(
                Collections.emptyList(),
                FIELD_NAME::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, KEY),
                FIELD_NAME::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(FIELD_NAME),
                FIELD_NAME::is));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, KEY, FIELD_NAME),
                FIELD_NAME::directLine));
        // from "value-string" to any tree-path test pattern
        testArguments.add(Pair.of(
                Collections.emptyList(),
                VALUE_STRING::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE),
                VALUE_STRING::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(VALUE_STRING),
                VALUE_STRING::is));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, VALUE_STRING),
                VALUE_STRING::directLine));
        // from "value-null" to any tree-path test pattern
        testArguments.add(Pair.of(
                Collections.emptyList(),
                VALUE_NULL::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE),
                VALUE_NULL::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(VALUE_NULL),
                VALUE_NULL::is));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, VALUE_NULL),
                VALUE_NULL::directLine));
        // from "value-number-f" to any tree-path test pattern
        testArguments.add(Pair.of(
                Collections.emptyList(),
                VALUE_NUMBER_F::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, NUMBER),
                VALUE_NUMBER_F::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(VALUE_NUMBER_F),
                VALUE_NUMBER_F::is));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, NUMBER, VALUE_NUMBER_F),
                VALUE_NUMBER_F::directLine));
        // from "value-number-i" to any tree-path test pattern
        testArguments.add(Pair.of(
                Collections.emptyList(),
                VALUE_NUMBER_I::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, NUMBER),
                VALUE_NUMBER_I::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(VALUE_NUMBER_I),
                VALUE_NUMBER_I::is));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, NUMBER, VALUE_NUMBER_I),
                VALUE_NUMBER_I::directLine));
        // from "value-number-decimal" to any tree-path test pattern
        testArguments.add(Pair.of(
                Collections.emptyList(),
                VALUE_NUMBER_DCML::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, NUMBER),
                VALUE_NUMBER_DCML::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(VALUE_NUMBER_DCML),
                VALUE_NUMBER_DCML::is));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, NUMBER, VALUE_NUMBER_DCML),
                VALUE_NUMBER_DCML::directLine));
        // from "value-true" to any tree-path test pattern
        testArguments.add(Pair.of(
                Collections.emptyList(),
                VALUE_TRUE::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, BOOLEAN),
                VALUE_TRUE::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(VALUE_TRUE),
                VALUE_TRUE::is));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, BOOLEAN, VALUE_TRUE),
                VALUE_TRUE::directLine));
        // from "value-false" to any tree-path test pattern
        testArguments.add(Pair.of(
                Collections.emptyList(),
                VALUE_FALSE::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, BOOLEAN),
                VALUE_FALSE::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(VALUE_FALSE),
                VALUE_FALSE::is));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, BOOLEAN, VALUE_FALSE),
                VALUE_FALSE::directLine));
        // from "value-json-array" to any tree-path test pattern
        testArguments.add(Pair.of(
                Collections.emptyList(),
                VALUE_JSON_ARRAY::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, TYPE_EMBEDDED),
                VALUE_JSON_ARRAY::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(VALUE_JSON_ARRAY),
                VALUE_JSON_ARRAY::is));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, TYPE_EMBEDDED, VALUE_JSON_ARRAY),
                VALUE_JSON_ARRAY::directLine));
        // from "value-json-object" to any tree-path test pattern
        testArguments.add(Pair.of(
                Collections.emptyList(),
                VALUE_JSON_OBJECT::contains));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, TYPE_EMBEDDED),
                VALUE_JSON_OBJECT::belongsTo));
        testArguments.add(Pair.of(
                Collections.singletonList(VALUE_JSON_OBJECT),
                VALUE_JSON_OBJECT::is));
        testArguments.add(Pair.of(
                Arrays.asList(TOKEN_ROOT, VALUE, TYPE_EMBEDDED, VALUE_JSON_OBJECT),
                VALUE_JSON_OBJECT::directLine));

        Assert.assertEquals(allSet.size() * 4, testArguments.size());

        testArguments.forEach(pair -> {
            Log.i(ToStringBuilder.reflectionToString(pair));
            final List<TypeTree> whiteList = pair.getLeft();
            final Function<TypeTree, Boolean> testLogic = pair.getRight();
            whiteList.forEach(elm -> Assert.assertTrue(testLogic.apply(elm)));
            allSet.stream().filter(elm -> !whiteList.contains(elm)).forEach(elm -> Assert.assertFalse(testLogic.apply(elm)));
        });

    }
}