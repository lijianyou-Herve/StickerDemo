package com.wanyueliang.stickerdemo.utils;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;


public class JsonUtils {

    public static <T> T fromJson(String json, Class<T> clazz) {
        T t = null;
        try {
            JsonReader jsonReader = new JsonReader(new StringReader(json));
            t = new Gson().fromJson(jsonReader, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static String toJson(Object src) {
        String json = null;
        try {
            json = new Gson().toJson(src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
