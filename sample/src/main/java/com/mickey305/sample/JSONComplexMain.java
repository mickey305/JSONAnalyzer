package com.mickey305.sample;

import com.mickey305.common.v2.json.Picker;
import com.mickey305.common.v2.json.model.Token;
import com.mickey305.common.v2.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;

public class JSONComplexMain {
    public static void main(String[] args) {
        String complexJson = "[\n" +
                "\t{\n" +
                "\t\t\"id\": \"0001\",\n" +
                "\t\t\"type\": \"donut\",\n" +
                "\t\t\"name\": \"Cake\",\n" +
                "\t\t\"ppu\": 0.55,\n" +
                "\t\t\"batters\":\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"batter\":\n" +
                "\t\t\t\t\t[\n" +
                "\t\t\t\t\t\t{ \"id\": \"1001\", \"type\": \"Regular\" },\n" +
                "\t\t\t\t\t\t{ \"id\": \"1002\", \"type\": \"Chocolate\" },\n" +
                "\t\t\t\t\t\t{ \"id\": \"1003\", \"type\": \"Blueberry\" },\n" +
                "\t\t\t\t\t\t{ \"id\": \"1004\", \"type\": \"Devil's Food\" }\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\"topping\":\n" +
                "\t\t\t[\n" +
                "\t\t\t\t{ \"id\": \"5001\", \"type\": \"None\" },\n" +
                "\t\t\t\t{ \"id\": \"5002\", \"type\": \"Glazed\" },\n" +
                "\t\t\t\t{ \"id\": \"5005\", \"type\": \"Sugar\" },\n" +
                "\t\t\t\t{ \"id\": \"5007\", \"type\": \"Powdered Sugar\" },\n" +
                "\t\t\t\t{ \"id\": \"5006\", \"type\": \"Chocolate with Sprinkles\" },\n" +
                "\t\t\t\t{ \"id\": \"5003\", \"type\": \"Chocolate\" },\n" +
                "\t\t\t\t{ \"id\": \"5004\", \"type\": \"Maple\" }\n" +
                "\t\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"0002\",\n" +
                "\t\t\"type\": \"donut\",\n" +
                "\t\t\"name\": \"Raised\",\n" +
                "\t\t\"ppu\": 0.55,\n" +
                "\t\t\"batters\":\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"batter\":\n" +
                "\t\t\t\t\t[\n" +
                "\t\t\t\t\t\t{ \"id\": \"1001\", \"type\": \"Regular\" }\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\"topping\":\n" +
                "\t\t\t[\n" +
                "\t\t\t\t{ \"id\": \"5001\", \"type\": \"None\" },\n" +
                "\t\t\t\t{ \"id\": \"5002\", \"type\": \"Glazed\" },\n" +
                "\t\t\t\t{ \"id\": \"5005\", \"type\": \"Sugar\" },\n" +
                "\t\t\t\t{ \"id\": \"5003\", \"type\": \"Chocolate\" },\n" +
                "\t\t\t\t{ \"id\": \"5004\", \"type\": \"Maple\" }\n" +
                "\t\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"id\": \"0003\",\n" +
                "\t\t\"type\": \"donut\",\n" +
                "\t\t\"name\": \"Old Fashioned\",\n" +
                "\t\t\"ppu\": 0.55,\n" +
                "\t\t\"batters\":\n" +
                "\t\t\t{\n" +
                "\t\t\t\t\"batter\":\n" +
                "\t\t\t\t\t[\n" +
                "\t\t\t\t\t\t{ \"id\": \"1001\", \"type\": \"Regular\" },\n" +
                "\t\t\t\t\t\t{ \"id\": \"1002\", \"type\": \"Chocolate\" }\n" +
                "\t\t\t\t\t]\n" +
                "\t\t\t},\n" +
                "\t\t\"topping\":\n" +
                "\t\t\t[\n" +
                "\t\t\t\t{ \"id\": \"5001\", \"type\": \"None\" },\n" +
                "\t\t\t\t{ \"id\": \"5002\", \"type\": \"Glazed\" },\n" +
                "\t\t\t\t{ \"id\": \"5003\", \"type\": \"Chocolate\" },\n" +
                "\t\t\t\t{ \"id\": \"5004\", \"type\": \"Maple\" }\n" +
                "\t\t\t]\n" +
                "\t}\n" +
                "]";

        // get the most topping food
        JSONArray ary = new JSONArray(complexJson);
        Log.i("Entry foods: ");
        ary.forEach(json -> Log.i("\t+ " + ((JSONObject) json).get("name").toString()));
        Picker<?> picker = new Picker<>(ary);
        List<Token> toppings = picker.getValues("topping");
        final Optional<Token> mostTopping = toppings.stream().max((o1, o2) -> {
            JSONArray ary1 = (JSONArray) o1.getObject();
            JSONArray ary2 = (JSONArray) o2.getObject();
            return ary1.length() - ary2.length();
        });
        ary.forEach(json -> {
            if (mostTopping.isPresent() && json.toString().contains(mostTopping.get().getString()))
                Log.i("Most topping food is " + ((JSONObject) json).get("name").toString());
        });
    }
}
