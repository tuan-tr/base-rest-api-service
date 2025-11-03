package com.tth.common.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfiguration {

	@Bean
	ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder()
				.featuresToEnable(
						DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)
				.featuresToDisable(
						SerializationFeature.FAIL_ON_EMPTY_BEANS,
						SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
						DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE,
						DeserializationFeature.ACCEPT_FLOAT_AS_INT,
						DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				// .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
				.serializationInclusion(JsonInclude.Include.NON_NULL)
				.createXmlMapper(false)
				.build();

		return objectMapper;
	}

}
