package com.mickey305.common.v2.json.io;

import com.mickey305.common.v2.exception.InsertObjectTypeException;
import com.mickey305.common.v2.json.model.Token;
import com.mickey305.common.v2.json.model.TokenSupplier;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TokenizerTest {
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private TokenSupplier<Token> supplier;

    @Before
    public void setUp() throws Exception {
        String jsonObjectStr = "{\"name\":{\"last\":\"tanaka\",\"first\":\"ichiro\"},\"info\":{\"license\":[\"AAA\",\"TOEIC(750)\"],\"weight\":68.5,\"height\":178}}";
        String jsonArrayStr = "[{},{\"name\":{\"last\":\"tanaka\",\"first\":\"ichiro\"},\"info\":{\"license\":[\"AAA\",\"TOEIC(750)\"],\"weight\":68.5,\"height\":178}}]";
        jsonObject = new JSONObject(jsonObjectStr);
        jsonArray = new JSONArray(jsonArrayStr);
        assertEquals(jsonObjectStr, jsonObject.toString());
        assertEquals(jsonArrayStr, jsonArray.toString());
        supplier = (Token::new);
    }

    @After
    public void tearDown() throws Exception {
        jsonObject = null;
        jsonArray = null;
        supplier = null;
    }

    @Test
    public void getInstance() throws Exception {
        // case 1
        try {
            new Tokenizer<>(jsonArray, supplier);
            assertTrue(true);
        } catch (InsertObjectTypeException e) {
            fail();
        }

        // case 2
        try {
            new Tokenizer<>(jsonObject, supplier);
            assertTrue(true);
        } catch (InsertObjectTypeException e) {
            fail();
        }

        // case 1
        try {
            new Tokenizer<>("String: Error Object", supplier);
            fail();
        } catch (InsertObjectTypeException e) {
            assertTrue(true);
        }
    }

    @Test
    public void hasNext() throws Exception {
        Tokenizer<Token> tokenizer;

        // case 1
        tokenizer = new Tokenizer<>(jsonObject, supplier);
        for (int i = 0; i < 5; i++)
            tokenizer.next();
        assertEquals(true, tokenizer.hasNext());

        // case 2
        tokenizer = new Tokenizer<>(jsonObject, supplier);
        while (tokenizer.hasNext())
            tokenizer.next();
        assertEquals(false, tokenizer.hasNext());
    }

    @Test
    public void next() throws Exception {
        Tokenizer<Token> tokenizer;

        // case 1
        tokenizer = new Tokenizer<>(jsonObject, supplier);
        for (int i = 0; i < 5; i++)
            tokenizer.next();
        assertEquals("first", tokenizer.next().getString());
        assertEquals("ichiro", tokenizer.next().getString());

        // case 2
        tokenizer = new Tokenizer<>(jsonObject, supplier);
        for (int i = 0; i < 6; i++)
            tokenizer.next();
        assertEquals("ichiro", tokenizer.next().getString());
        assertEquals("}", tokenizer.next().getString());

        // case 3
        tokenizer = new Tokenizer<>(jsonObject, supplier);
        assertEquals("{", tokenizer.next().getString());
        assertEquals("name", tokenizer.next().getString());
    }

    @Test
    public void now() throws Exception {
        Tokenizer<Token> tokenizer;

        // case 1
        tokenizer = new Tokenizer<>(jsonObject, supplier);
        for (int i = 0; i < 6; i++)
            tokenizer.next();
        assertEquals("first", tokenizer.now().getString());
        assertEquals("first", tokenizer.now().getString());

        // case 2
        tokenizer = new Tokenizer<>(jsonObject, supplier);
        for (int i = 0; i < 7; i++)
            tokenizer.next();
        assertEquals("ichiro", tokenizer.now().getString());
        assertEquals("ichiro", tokenizer.now().getString());

        // case 3
        tokenizer = new Tokenizer<>(jsonObject, supplier);
        tokenizer.next();
        assertEquals("{", tokenizer.now().getString());
        assertEquals("{", tokenizer.now().getString());

        // case 4
        tokenizer = new Tokenizer<>(jsonObject, supplier);
        try {
            tokenizer.now();
            fail();
        } catch (NullPointerException e) {
            assertTrue(true);
        }
    }

    @Test
    public void peek() throws Exception {
        Tokenizer<Token> tokenizer;

        // case 1
        tokenizer = new Tokenizer<>(jsonObject, supplier);
        for (int i = 0; i < 5; i++)
            tokenizer.next();
        assertEquals("first", tokenizer.peek().getString());
        assertEquals("first", tokenizer.peek().getString());

        // case 2
        tokenizer = new Tokenizer<>(jsonObject, supplier);
        for (int i = 0; i < 6; i++)
            tokenizer.next();
        assertEquals("ichiro", tokenizer.peek().getString());
        assertEquals("ichiro", tokenizer.peek().getString());

        // case 3
        tokenizer = new Tokenizer<>(jsonObject, supplier);
        assertEquals("{", tokenizer.peek().getString());
        assertEquals("{", tokenizer.peek().getString());
    }

    @Test
    public void skip() throws Exception {
        Tokenizer<Token> tokenizer;

        // case 1
        tokenizer = new Tokenizer<>(jsonObject, supplier);
        tokenizer.skip(6);
        assertEquals("first", tokenizer.now().getString());
    }

    @Test
    public void getIndex() throws Exception {

    }

    @Test
    public void setIterationCallback() throws Exception {

    }

    @Test
    public void equalsAndClone() {
        Tokenizer<Token> tokenizer11 = new Tokenizer<>(jsonArray, supplier);
        tokenizer11.next();

        // case 1
        Tokenizer<Token> tokenizer12 = tokenizer11;
        tokenizer12.next();
        assertEquals(true, tokenizer11.equals(tokenizer12));

        // case 2
        Tokenizer<Token> tokenizer21 = tokenizer11.clone();
        tokenizer21.next();
        assertEquals(false, tokenizer11.equals(tokenizer21));
    }

}