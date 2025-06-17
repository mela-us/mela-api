package com.hcmus.mela.shared.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    public static List<String> extractImageSources(String text) {
        List<String> imageSources = new ArrayList<>();
        String regex = "<img\\s+src=['\"]([^'\"]+)['\"]>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            imageSources.add(matcher.group(1)); // Capture the src value
        }

        return imageSources;
    }

    public static Map<String, Object> extractResponseFromJsonText(String jsonString, String... keys) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        String dataString = LaTeXUtils.normalizeLaTeX(jsonString);
        Map<String, Object> data = gson.fromJson(dataString, type);

        Map<String, Object> result = new HashMap<>();
        for (String key : keys) {
            if (data.containsKey(key) && data.get(key) != null) {
                result.put(key, data.get(key));
            }
        }
        return result.isEmpty() ? Collections.emptyMap() : result;
    }

    public static String normalizeText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.replaceAll("\\s+", " ").trim();
    }
}
