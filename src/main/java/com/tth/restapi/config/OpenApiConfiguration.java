package com.tth.restapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
@SecurityScheme(name = "bearerToken", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
// @SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, bearerFormat = "basicAuth", scheme = "basic")
@OpenAPIDefinition(info = @Info(title = "APIs", version = "0.0.1"),
		servers = @Server(url = "${server.servlet.context-path}", description = "Default Server URL"),
		security = @SecurityRequirement(name = "bearerToken")
		// security = @SecurityRequirement(name = "basicAuth")
)
public class OpenApiConfiguration {

	@Value("${spring.web.locale}")
	private String defaultLanguage;

	// @Bean
	ModelResolver modelResolver(ObjectMapper objectMapper) {
		return new ModelResolver(objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE));
	}

	@Bean
	OperationCustomizer customize() {
		return (operation, handlerMethod) -> operation.addParametersItem(
				new Parameter()
						.in("header")
						.name(HttpHeaders.ACCEPT_LANGUAGE)
						.required(false)
						.example(defaultLanguage));
	}

}
