package com.mickey305.common.v2.json;

import com.mickey305.common.v2.exception.JSONTokenTypeException;
import com.mickey305.common.v2.json.model.TYPE;
import com.mickey305.common.v2.json.model.Token;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PickerTest {
    private JSONObject jsonObject;
    private JSONArray jsonArray;

    @Before
    public void setUp() throws Exception {
        String jsonObjectStr = "{\"name\":{\"last\":\"tanaka\",\"first\":\"ichiro\"},\"info\":{\"license\":[\"AAA\",\"TOEIC(750)\"],\"weight\":68.5,\"height\":178}}";
        String jsonArrayStr = "[{\"empty\":24},{\"name\":{\"last\":\"tanaka\",\"first\":\"ichiro\"},\"info\":{\"license\":[\"AAA\",\"TOEIC(750)\"],\"weight\":68.5,\"age\":24,\"height\":178}}]";
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
    public void buildTokenList() throws Exception {

    }

    @Test
    public void createNonKeyValueList() throws Exception {

    }

    @Test
    public void equalsAndClone() throws Exception {
        Picker<?> picker11 = new Picker<>(jsonArray);
        picker11.setOverwriteInterface(String::contains);

        // case 1
        Picker<?> picker12 = picker11;
        picker12.setOverwriteInterface(String::equals);
        assertEquals(true, picker11.equals(picker12));

        // case 2
        Picker<?> picker21 = picker11.clone();
        picker12.setOverwriteInterface(String::endsWith);
        assertEquals(false, picker11.equals(picker21));
    }

    @Test
    public void getAllKeyList() throws Exception {

    }

    @Test
    public void getAllKeyHashList() throws Exception {

    }

    @Test
    public void getAllKeyHashSet() throws Exception {

    }

    @Test
    public void getAllValueList() throws Exception {
        Picker<?> picker;
        List<Token> list;

        // case 1
        picker = new Picker<>(jsonArray);
        list = picker.getAllValueList();

        assertEquals(13, list.size());

        assertEquals(true, 1 <= list.stream().filter(token -> token.getString().equals("{\"empty\":24}")).count());
        assertEquals(true, 1 <= list.stream().filter(token -> token.getString().equals("{\"name\":{\"last\":\"tanaka\",\"first\":\"ichiro\"},\"info\":{\"license\":[\"AAA\",\"TOEIC(750)\"],\"weight\":68.5,\"age\":24,\"height\":178}}")).count());
        assertEquals(true, 1 <= list.stream().filter(token -> token.getString().equals("{\"last\":\"tanaka\",\"first\":\"ichiro\"}")).count());
        assertEquals(true, 1 <= list.stream().filter(token -> token.getString().equals("{\"license\":[\"AAA\",\"TOEIC(750)\"],\"weight\":68.5,\"age\":24,\"height\":178}")).count());
        assertEquals(true, 1 <= list.stream().filter(token -> token.getString().equals("tanaka")).count());
        assertEquals(true, 1 <= list.stream().filter(token -> token.getString().equals("ichiro")).count());
        assertEquals(true, 1 <= list.stream().filter(token -> token.getString().equals("[\"AAA\",\"TOEIC(750)\"]")).count());
        assertEquals(true, 1 <= list.stream().filter(token -> token.getString().equals("68.5")).count());
        assertEquals(true, 1 <= list.stream().filter(token -> token.getString().equals("24")).count()); // * 2
        assertEquals(true, 1 <= list.stream().filter(token -> token.getString().equals("178")).count());
        assertEquals(true, 1 <= list.stream().filter(token -> token.getString().equals("AAA")).count());
        assertEquals(true, 1 <= list.stream().filter(token -> token.getString().equals("TOEIC(750)")).count());

        assertEquals(4, list.stream().filter(token -> token.getType() == TYPE.VALUE_JSON_OBJECT).count());
        assertEquals(4, list.stream().filter(token -> token.getType() == TYPE.VALUE_STRING).count());
        assertEquals(1, list.stream().filter(token -> token.getType() == TYPE.VALUE_JSON_ARRAY).count());
        assertEquals(3, list.stream().filter(token -> token.getType() == TYPE.VALUE_NUMBER_I).count());
        assertEquals(1, list.stream().filter(token -> token.getType() == TYPE.VALUE_NUMBER_F).count());

        assertEquals(true, list.size() == list.stream().filter(token -> token.getType().isValue()).count());
    }

    @Test
    public void getAllValueHashList() throws Exception {
        Picker<?> picker;
        List<Token> list;

        // case 1
        picker = new Picker<>(jsonArray);
        list = picker.getAllValueHashList();

        assertEquals(12, list.size());

        assertEquals(true, 1 == list.stream().filter(token -> token.getString().equals("{\"empty\":24}")).count());
        assertEquals(true, 1 == list.stream().filter(token -> token.getString().equals("{\"name\":{\"last\":\"tanaka\",\"first\":\"ichiro\"},\"info\":{\"license\":[\"AAA\",\"TOEIC(750)\"],\"weight\":68.5,\"age\":24,\"height\":178}}")).count());
        assertEquals(true, 1 == list.stream().filter(token -> token.getString().equals("{\"last\":\"tanaka\",\"first\":\"ichiro\"}")).count());
        assertEquals(true, 1 == list.stream().filter(token -> token.getString().equals("{\"license\":[\"AAA\",\"TOEIC(750)\"],\"weight\":68.5,\"age\":24,\"height\":178}")).count());
        assertEquals(true, 1 == list.stream().filter(token -> token.getString().equals("tanaka")).count());
        assertEquals(true, 1 == list.stream().filter(token -> token.getString().equals("ichiro")).count());
        assertEquals(true, 1 == list.stream().filter(token -> token.getString().equals("[\"AAA\",\"TOEIC(750)\"]")).count());
        assertEquals(true, 1 == list.stream().filter(token -> token.getString().equals("68.5")).count());
        assertEquals(true, 1 == list.stream().filter(token -> token.getString().equals("24")).count());
        assertEquals(true, 1 == list.stream().filter(token -> token.getString().equals("178")).count());
        assertEquals(true, 1 == list.stream().filter(token -> token.getString().equals("AAA")).count());
        assertEquals(true, 1 == list.stream().filter(token -> token.getString().equals("TOEIC(750)")).count());

        assertEquals(4, list.stream().filter(token -> token.getType() == TYPE.VALUE_JSON_OBJECT).count());
        assertEquals(4, list.stream().filter(token -> token.getType() == TYPE.VALUE_STRING).count());
        assertEquals(1, list.stream().filter(token -> token.getType() == TYPE.VALUE_JSON_ARRAY).count());
        assertEquals(2, list.stream().filter(token -> token.getType() == TYPE.VALUE_NUMBER_I).count());
        assertEquals(1, list.stream().filter(token -> token.getType() == TYPE.VALUE_NUMBER_F).count());

        assertEquals(true, list.size() == list.stream().filter(token -> token.getType().isValue()).count());
    }

    @Test
    public void getAllValueHashSet() throws Exception {

    }

    @Test
    public void getValues() throws Exception {
        Picker<?> picker;
        List<Token> list;

        // case 1-1
        picker = new Picker<>(jsonArray);
        list = picker.getValues("license");
        assertEquals(1, list.size());
        assertEquals("[\"AAA\",\"TOEIC(750)\"]", list.get(0).getString());
        assertEquals(TYPE.VALUE_JSON_ARRAY, list.get(0).getType());

        // case 1-2
        picker = new Picker<>(jsonArray);
        try {
            picker.getValues("nothingKeyWord");
            fail();
        } catch (JSONTokenTypeException e) {
            assertTrue(true);
        }

        // case 2-1
        picker = new Picker<>(jsonArray);
        List<String> array = new ArrayList<>();
        array.add("name");
        array.add("first");
        list = picker.getValues(array);
        assertEquals(1, list.size());
        assertEquals("ichiro", list.get(0).getString());
        assertEquals(TYPE.VALUE_STRING, list.get(0).getType());

        // case 2-2
        picker = new Picker<>(jsonArray);
        list = picker.getValues("name", "first");
        assertEquals(1, list.size());
        assertEquals("ichiro", list.get(0).getString());
        assertEquals(TYPE.VALUE_STRING, list.get(0).getType());

        // case 3
        picker = new Picker<>(jsonArray);
        picker.setOverwriteInterface(String::contains);
        list = picker.getValues("eight");
        assertEquals(2, list.size());
        assertEquals(true, 1 == list.stream().filter(token -> token.getString().equals("68.5")).count());
        assertEquals(true, 1 == list.stream().filter(token -> token.getString().equals("178")).count());
    }

    @Test
    public void isExistKey() throws Exception {

    }

    @Test
    public void isExistAllKeys() throws Exception {

    }

    @Test
    public void isExistAllKeys1() throws Exception {

    }

    @Test
    public void searchValues() throws Exception {

    }

    @Test
    public void searchValues1() throws Exception {

    }

    @Test
    public void searchValues2() throws Exception {

    }

    @Test
    public void generateEmbeddedValue() throws Exception {

    }

    @Test
    public void setOverwriteInterface() throws Exception {

    }

}