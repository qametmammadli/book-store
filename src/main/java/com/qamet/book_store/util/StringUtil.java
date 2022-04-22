package com.qamet.book_store.util;

public interface StringUtil
{
    /**
     * Removes any leading and trailing spaces and
     * replaces 2 or more white spaces with single space
     * but keeps single line breaks
     *
     * @param str the string that should be normalized
     * @return normalized string
     */
    static String trimLeadingTrailingExtraWhitespace(String str)
    {
        return str
                .trim()
                .replaceAll("^\\s+|\\s+$|\\s*(\n)\\s*|(\\s)\\s*", "$1$2")
                .replace("\t", " ");
    }

    /**
     * Converts a string to snake_case
     *
     * @param str a string
     *
     * @return snake_case string
     */
    static String toSnakeCase(final String str)
    {
        String regex = "(\\p{Ll})(\\p{Lu})";
        String replacement = "$1_$2";

        return str.replaceAll(regex, replacement).toLowerCase();
    }

}
