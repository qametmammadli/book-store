package com.qamet.book_store.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class StringUtilUnitTest {

    @Test
    void shouldTrimLeadingTrailingExtraWhitespace() {
        String withWhitespace = "  \n \t   \t  Test book-store ";
        String expected = "Test book-store";

        String trimmedStr = StringUtil.trimLeadingTrailingExtraWhitespace(withWhitespace);

        assertEquals(expected, trimmedStr);
    }


    @Test
    void shouldTrimLeadingTrailingExtraWhitespaceAndMultipleLineBreaks() {
        String withWhitespaceAndMultipleLineBreaks = "   \t   \nTest \n\n \t \n  \t String \t    Dev 2";
        String expected = "Test\nString Dev 2";

        String trimmedStr = StringUtil.trimLeadingTrailingExtraWhitespace(withWhitespaceAndMultipleLineBreaks);


        assertEquals(expected, trimmedStr);
    }

    @Test
    void shouldConvertToSnakeCase() {
        String camelCaseStr = "strTestDev";
        String expected = "str_test_dev";

        String snake_case_str = StringUtil.toSnakeCase(camelCaseStr);

        assertEquals(expected, snake_case_str);
    }
}