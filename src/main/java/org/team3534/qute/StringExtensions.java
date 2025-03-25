package org.team3534.qute;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension
public class StringExtensions {
    public static String upper(String str) {
        return str != null ? str.toUpperCase() : null;
    }

    public static String lower(String str) {
        return str != null ? str.toLowerCase() : null;
    }
}
