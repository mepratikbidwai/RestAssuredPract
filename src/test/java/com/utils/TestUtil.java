package com.utils;

import io.restassured.path.json.JsonPath;

public class TestUtil {
    public static JsonPath rawToJSON(String rawResponse){
        JsonPath jsonPath = new JsonPath(rawResponse);
        return jsonPath;
    }
}
