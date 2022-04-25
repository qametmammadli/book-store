package com.qamet.book_store.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.io.IOException;

public interface JsonUtil
{
    /**
     * Converts object to JSON String
     *
     * @param object java object that should be converted
     * @return json bytes
     */
    static String convertObjectToJson(Object object)
            throws IOException
    {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return jsonMapper.writeValueAsString(object);
    }
}
