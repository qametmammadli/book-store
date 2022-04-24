package com.qamet.book_store.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.qamet.book_store.entity.User;
import com.qamet.book_store.rest.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.jackson.ProblemModule;

@Configuration
public class MapperConfiguration {
    @Bean
    public ProblemModule problemModule() {
        return new ProblemModule();
    }

    /*
     * trimming extra whitespaces from all strings.
     */
    @Bean
    public StringInitializerModule stringTrimModule() {
        return new StringInitializerModule();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.addMappings(new PropertyMap<User, UserDTO>() {
            @Override
            protected void configure() {
                skip(destination.getPassword());
            }
        });
        return modelMapper;
    }
}
