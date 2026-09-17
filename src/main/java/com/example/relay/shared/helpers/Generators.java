package com.example.relay.shared.helpers;

public class Generators {
    public static String randomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder suffix = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            suffix.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return suffix.toString();
    }
}