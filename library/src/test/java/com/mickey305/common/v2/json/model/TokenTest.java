package com.mickey305.common.v2.json.model;

import com.mickey305.common.v2.exception.JSONTokenTypeException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TokenTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void equalsAndClone() throws Exception {
        Token token11 = new Token('}');
        token11.setDepth(-1);

        // case 1
        Token token12 = token11;
        token12.setDepth(1);
        assertEquals(true, token11.equals(token12));

        // case 2
        Token token21 = token11.clone();
        token21.setDepth(2);
        assertEquals(false, token11.equals(token21));
    }

    @Test
    public void getString() throws Exception {
        Token token;

        // case 1
        token = new Token('{');
        assertEquals("{", token.getString());
        token = new Token('}');
        assertEquals("}", token.getString());
        token = new Token('[');
        assertEquals("[", token.getString());
        token = new Token(']');
        assertEquals("]", token.getString());
        token = new Token("null");
        assertEquals("null", token.getString());
        token = new Token("true");
        assertEquals("true", token.getString());
        token = new Token("false");
        assertEquals("false", token.getString());

        // case 2
        token = new Token(11);
        assertEquals("11", token.getString());
        token = new Token((float) 1.1);
        assertEquals("1.1", token.getString());

        // case 3
        token = new Token(Type.VALUE_NULL, "null");
        assertEquals("null", token.getString());
        token = new Token(Type.VALUE_JSON_ARRAY, "[\"AAAA\",\"BBBB\"]");
        assertEquals("[\"AAAA\",\"BBBB\"]", token.getString());

        // case 4
        try {
            new Token("test");
            fail();
        } catch (JSONTokenTypeException e) {
            assertTrue(true);
        }

        // case 5
        token = new Token(true);
        assertEquals("true", token.getString());
        token = new Token(false);
        assertEquals("false", token.getString());
    }

    @Test
    public void getObject() throws Exception {
        Token token;

        // case 1
        token = new Token(11);
        assertEquals(true, token.getObject() instanceof Integer);
        token = new Token((float) 1.1);
        assertEquals(true, token.getObject() instanceof Float);
        token = new Token(true);
        assertEquals(true, token.getObject() instanceof Boolean);
        token = new Token(false);
        assertEquals(true, token.getObject() instanceof Boolean);
        token = new Token(Type.VALUE_JSON_ARRAY, "[\"AAAA\",\"BBBB\"]");
        assertEquals(true, token.getObject() instanceof JSONArray);
        token = new Token(Type.VALUE_JSON_OBJECT, "{\"name\":\"michael\",\"from\":\"USA\"}");
        assertEquals(true, token.getObject() instanceof JSONObject);

        // case 2
        token = new Token(Type.VALUE_JSON_OBJECT, "[\"AAAA\",\"BBBB\"]");
        try {
            token.getObject();
            fail();
        } catch (JSONException e) {
            assertTrue(true);
        }
    }

    @Test
    public void getType() throws Exception {
        Token token;

        // case 1
        token = new Token(11);
        assertEquals(Type.VALUE_NUMBER_I, token.getType());
        token = new Token((float) 1.1);
        assertEquals(Type.VALUE_NUMBER_F, token.getType());
        token = new Token(true);
        assertEquals(Type.VALUE_TRUE, token.getType());
        token = new Token(false);
        assertEquals(Type.VALUE_FALSE, token.getType());
        token = new Token('{');
        assertEquals(Type.START_OBJECT, token.getType());
        token = new Token('}');
        assertEquals(Type.END_OBJECT, token.getType());
        token = new Token('[');
        assertEquals(Type.START_ARRAY, token.getType());
        token = new Token(']');
        assertEquals(Type.END_ARRAY, token.getType());
        token = new Token("null");
        assertEquals(Type.VALUE_NULL, token.getType());
    }

    @Test
    public void setType() throws Exception {

    }

    @Test
    public void getIndexNumber() throws Exception {

    }

    @Test
    public void setIndexNumber() throws Exception {

    }

}