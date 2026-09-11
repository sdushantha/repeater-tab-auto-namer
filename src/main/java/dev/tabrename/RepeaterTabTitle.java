package dev.tabrename;

import burp.api.montoya.http.message.requests.HttpRequest;

import java.util.Locale;

final class RepeaterTabTitle {
    private RepeaterTabTitle() {
    }

    static String from(HttpRequest request) {
        return from(request.method(), request.pathWithoutQuery());
    }

    static String from(String method, String path) {
        String normalizedMethod = normalizeMethod(method);
        String normalizedPath = normalizePath(path);
        return normalizedMethod + " " + normalizedPath;
    }

    private static String normalizeMethod(String method) {
        if (method == null || method.isBlank()) {
            return "REQUEST";
        }

        return stripControlCharacters(method).trim().toUpperCase(Locale.ROOT);
    }

    private static String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }

        String normalized = stripControlCharacters(path).trim();
        return normalized.startsWith("/") || normalized.equals("*") ? normalized : "/" + normalized;
    }

    private static String stripControlCharacters(String value) {
        return value.replaceAll("[\\p{Cntrl}]", "");
    }
}
