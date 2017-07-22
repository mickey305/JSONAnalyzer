package com.mickey305.common.v2.json.io;

import com.mickey305.common.v2.exception.InsertObjectTypeException;
import com.mickey305.common.v2.json.model.Type;
import com.mickey305.common.v2.json.model.Token;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TokenListBuilderTest {
    private JSONObject jsonObject;
    private JSONArray jsonArray;

    @Before
    public void setUp() throws Exception {
        String jsonObjectStr = "{\"name\":{\"last\":\"tanaka\",\"first\":\"ichiro\"}}";
        String jsonArrayStr = "[{},{\"name\":{\"last\":\"tanaka\",\"first\":\"ichiro\"},\"id\":10019}]";
        jsonObject = new JSONObject(jsonObjectStr);
        jsonArray = new JSONArray(jsonArrayStr);
        assertEquals(jsonObjectStr, jsonObject.toString());
        assertEquals(jsonArrayStr, jsonArray.toString());
    }

    @After
    public void tearDown() throws Exception {
        jsonObject = null;
        jsonArray = null;
    }

    @Test
    public void getInstance() throws Exception {
        List<Token> list = new ArrayList<>();

        // case 1
        try {
            TokenListBuilder.build(jsonObject, list);
            assertTrue(true);
        } catch (InsertObjectTypeException e) {
            fail();
        }

        // case 2
        try {
            TokenListBuilder.build(jsonArray, list);
            assertTrue(true);
        } catch (InsertObjectTypeException e) {
            fail();
        }

        // case 3
        try {
            TokenListBuilder.build("Error Text", list);
            fail();
        } catch (InsertObjectTypeException e) {
            assertTrue(true);
        }
    }

    @Test
    public void build() throws Exception {
        List<Token> list = new ArrayList<>();

        // case 1
        TokenListBuilder.build(jsonObject, list);

        assertEquals(9, list.size());

        assertEquals("{", list.get(0).getString());
        assertEquals("name", list.get(1).getString());
        assertEquals("{", list.get(2).getString());
        assertEquals("last", list.get(3).getString());
        assertEquals("tanaka", list.get(4).getString());
        assertEquals("first", list.get(5).getString());
        assertEquals("ichiro", list.get(6).getString());
        assertEquals("}", list.get(7).getString());
        assertEquals("}", list.get(8).getString());

        assertEquals(Type.START_OBJECT, list.get(0).getType());
        assertEquals(Type.FIELD_NAME, list.get(1).getType());
        assertEquals(Type.START_OBJECT, list.get(2).getType());
        assertEquals(Type.FIELD_NAME, list.get(3).getType());
        assertEquals(Type.VALUE_STRING, list.get(4).getType());
        assertEquals(Type.FIELD_NAME, list.get(5).getType());
        assertEquals(Type.VALUE_STRING, list.get(6).getType());
        assertEquals(Type.END_OBJECT, list.get(7).getType());
        assertEquals(Type.END_OBJECT, list.get(8).getType());

        //// depth data test
        assertEquals(0, list.get(0).getDepth());
        assertEquals(0, list.get(1).getDepth());
        assertEquals(1, list.get(2).getDepth());
        assertEquals(1, list.get(3).getDepth());
        assertEquals(1, list.get(4).getDepth());
        assertEquals(1, list.get(5).getDepth());
        assertEquals(1, list.get(6).getDepth());
        assertEquals(1, list.get(7).getDepth());
        assertEquals(0, list.get(8).getDepth());
    }

    @Test
    public void buildArrayList() throws Exception {

    }

    @Test
    public void buildLinkedList() throws Exception {

    }

}