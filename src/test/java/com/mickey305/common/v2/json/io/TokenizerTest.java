package com.mickey305.common.v2.json.io;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TokenizerTest {
    private JSONObject jsonObject;
    private JSONArray jsonArray;

    @Before
    public void setUp() throws Exception {
        String jsonObjectStr = "{\"name\":{\"last\":\"tanaka\",\"first\":\"ichiro\"},\"info\":{\"license\":[\"AAA\",\"TOEIC(750)\"],\"weight\":68.5,\"height\":178}}";
        String jsonArrayStr = "[{},{\"name\":{\"last\":\"tanaka\",\"first\":\"ichiro\"},\"info\":{\"license\":[\"AAA\",\"TOEIC(750)\"],\"weight\":68.5,\"height\":178}}]";
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
    public void hasNext() throws Exception {
        Tokenizer<?> tokenizer;

        // case 1
        tokenizer = new Tokenizer<>(jsonObject);
        for (int i = 0; i < 5; i++)
            tokenizer.next();
        assertEquals(true, tokenizer.hasNext());

        // case 2
        tokenizer = new Tokenizer<>(jsonObject);
        while (tokenizer.hasNext())
            tokenizer.next();
        assertEquals(false, tokenizer.hasNext());
    }

    @Test
    public void next() throws Exception {
        Tokenizer<?> tokenizer;

        // case 1
        tokenizer = new Tokenizer<>(jsonObject);
        for (int i = 0; i < 5; i++)
            tokenizer.next();
        assertEquals("first", tokenizer.next().getString());
        assertEquals("ichiro", tokenizer.next().getString());

        // case 2
        tokenizer = new Tokenizer<>(jsonObject);
        for (int i = 0; i < 6; i++)
            tokenizer.next();
        assertEquals("ichiro", tokenizer.next().getString());
        assertEquals("}", tokenizer.next().getString());

        // case 3
        tokenizer = new Tokenizer<>(jsonObject);
        assertEquals("{", tokenizer.next().getString());
        assertEquals("name", tokenizer.next().getString());
    }

    @Test
    public void now() throws Exception {
        Tokenizer<?> tokenizer;

        // case 1
        tokenizer = new Tokenizer<>(jsonObject);
        for (int i = 0; i < 6; i++)
            tokenizer.next();
        assertEquals("first", tokenizer.now().getString());
        assertEquals("first", tokenizer.now().getString());

        // case 2
        tokenizer = new Tokenizer<>(jsonObject);
        for (int i = 0; i < 7; i++)
            tokenizer.next();
        assertEquals("ichiro", tokenizer.now().getString());
        assertEquals("ichiro", tokenizer.now().getString());

        // case 3
        tokenizer = new Tokenizer<>(jsonObject);
        tokenizer.next();
        assertEquals("{", tokenizer.now().getString());
        assertEquals("{", tokenizer.now().getString());

        // case 4
        tokenizer = new Tokenizer<>(jsonObject);
        try {
            tokenizer.now();
            fail();
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void peek() throws Exception {
        Tokenizer<?> tokenizer;

        // case 1
        tokenizer = new Tokenizer<>(jsonObject);
        for (int i = 0; i < 5; i++)
            tokenizer.next();
        assertEquals("first", tokenizer.peek().getString());
        assertEquals("first", tokenizer.peek().getString());

        // case 2
        tokenizer = new Tokenizer<>(jsonObject);
        for (int i = 0; i < 6; i++)
            tokenizer.next();
        assertEquals("ichiro", tokenizer.peek().getString());
        assertEquals("ichiro", tokenizer.peek().getString());

        // case 3
        tokenizer = new Tokenizer<>(jsonObject);
        assertEquals("{", tokenizer.peek().getString());
        assertEquals("{", tokenizer.peek().getString());
    }

    @Test
    public void skip() throws Exception {
        Tokenizer<?> tokenizer;

        // case 1
        tokenizer = new Tokenizer<>(jsonObject);
        tokenizer.skip(6);
        assertEquals("first", tokenizer.now().getString());
    }

    @Test
    public void getIndex() throws Exception {

    }

    @Test
    public void setIterationCallback() throws Exception {

    }

}