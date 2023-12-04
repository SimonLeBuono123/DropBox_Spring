package com.example.dropboxSpring.utils;

/*
Utility class for the token string
Only usage for now is the static method removeEmptySpace
which is used when removing empty space when getting the token from the authorization header
in postman.
 */
public class TokenStringUtils {
    public static String removeEmptySpace(String token){
        return token.split(" ")[1].trim();
    }

}
