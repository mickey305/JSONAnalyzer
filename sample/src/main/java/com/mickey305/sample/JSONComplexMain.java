package com.mickey305.sample;

import com.mickey305.common.v2.json.ArrayFinder;
import com.mickey305.common.v2.json.ObjectFinder;
import com.mickey305.common.v2.json.Picker;
import com.mickey305.common.v2.json.io.TokenListBuilder;
import com.mickey305.common.v2.json.model.Token;
import com.mickey305.common.v2.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Picker<ChildToken> picker = new Picker<>(ary, ChildToken::new);
        List<ChildToken> toppings = picker.getValues("topping");
        final Optional<ChildToken> mostTopping = toppings.stream().max((o1, o2) -> {
            JSONArray ary1 = (JSONArray) o1.getObject();
            JSONArray ary2 = (JSONArray) o2.getObject();
            return ary1.length() - ary2.length();
        });
        ary.forEach(json -> {
            if (mostTopping.isPresent() && json.toString().contains(mostTopping.get().getString()))
                Log.i("Most topping food is " + ((JSONObject) json).get("name").toString());
        });

        Log.i("-------------------------------------------------------------------------------------------");
        ObjectFinder<Token> finder = new ObjectFinder<>(ary, Token::new);
        finder.setOverwriteInterface((targetTok, query) -> targetTok.getString().equals(query) && targetTok.getDepth() == 1);
        List<Token> objects = finder.findByKey("type");
        objects.forEach(obj -> Log.i(obj.getString()));
        JSONObject tmp = new JSONObject("{a:{b:1,c:2,d:3,e:[12,{f:4,g:5},{h:[12]}]}}");
        List<Token> tmp2 = TokenListBuilder.buildArrayList(tmp, Token::new);
        List<Integer> tmp3 = tmp2.stream().map(Token::getDepth).collect(Collectors.toList());
        Log.i(tmp3.toString());

        Log.i("-------------------------------------------------------------------------------------------");
        ArrayFinder<Token> finder2 = new ArrayFinder<>(ary, Token::new);
        List<Token> objects2 = finder2.findByValue("{\"id\":\"1001\",\"type\":\"Regular\"}");
//        List<Token> objects2 = finder2.findByValue("None");
        objects2.forEach(obj -> Log.i(obj.getString()));
    }
}
