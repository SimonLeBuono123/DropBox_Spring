package com.example.dropboxSpring.utils;

/**
 * Utility class for the token string
 * Only usage for now is the static method removeEmptySpace
 * which is used when removing the empty space when getting the token from the authorization header
 * in postman. It also removes the first  part of the token "bearer" as it not is needed when
 * choosing the type "bearer token" in authorization. You do not need to use this method if you use the normal
 * header with authorization
 */
public class TokenStringUtils {
    public static String removeEmptySpace(String token){
        return token.split(" ")[1].trim();
    }

}
