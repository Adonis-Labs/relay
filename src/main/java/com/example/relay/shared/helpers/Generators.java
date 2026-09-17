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

    /// Builds a URL-safe slug from {@code input} (e.g. an entity name), appending a random
    /// alphanumeric suffix so slugs stay unique even when two rows share a display name.
    /// {@code fallback} is used when {@code input} has no sluggable characters (e.g. all-emoji names).
    public static String slug(String input, String fallback, int suffixLength) {
        String base = slugify(input, fallback);
        return suffixLength > 0 ? base + "-" + randomAlphanumeric(suffixLength) : base;
    }

    private static String slugify(String input, String fallback) {
        String transformed = input
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "") // strip punctuation
                .replaceAll("\\s+", "-")          // whitespace -> hyphen
                .replaceAll("-+", "-")            // collapse repeated hyphens
                .replaceAll("^-|-$", "");         // trim leading/trailing hyphen
        if (transformed.isEmpty())
            transformed = fallback;
        return transformed.substring(0, Math.min(transformed.length(), 50));
    }
}