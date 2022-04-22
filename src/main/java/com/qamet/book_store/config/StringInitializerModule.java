package com.qamet.book_store.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.qamet.book_store.util.StringUtil;

import java.io.IOException;


/**
 * Globally trim leading/trailing/extra whitespace from all string properties
 */
public class StringInitializerModule extends SimpleModule
{

    private static final long serialVersionUID = 1L;

    public StringInitializerModule()
    {
        addDeserializer(String.class, new ScalarDeserializer());
    }

    private static class ScalarDeserializer extends StdScalarDeserializer<String>
    {

        private static final long serialVersionUID = 1L;

        protected ScalarDeserializer()
        {
            super(String.class);
        }

        @Override
        public String deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException
        {
            return StringUtil.trimLeadingTrailingExtraWhitespace(jsonParser.getValueAsString());
        }
    }
}
