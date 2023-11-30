package com.example.dropboxSpring.utils;

public class TokenStringUtils {
    public static String removeEmptySpace(String token){
        return token.split(" ")[1].trim();
    }

}
