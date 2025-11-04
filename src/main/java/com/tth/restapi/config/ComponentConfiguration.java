package com.tth.restapi.config;

import com.tth.common.auth.AuthEnviroment;
import com.tth.common.i18n.I18nConfiguration;
import com.tth.common.i18n.Translator;
import com.tth.common.jackson.JacksonConfiguration;
import com.tth.common.jpa.CustomJpaRepositoryProvider;
import com.tth.common.servletfilter.RequestIdHandlerFilter;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware", dateTimeProviderRef = "dateTimeProvider")
@EntityScan(basePackages = "com.tth.persistence.entity")
@EnableJpaRepositories(basePackages = "com.tth.persistence.repository")
@ConfigurationPropertiesScan(basePackageClasses = {
				AuthEnviroment.class
		})
@ComponentScan(basePackages = {
				
		}, basePackageClasses = {
				RequestIdHandlerFilter.class,
				I18nConfiguration.class,
				Translator.class,
				JacksonConfiguration.class,
				CustomJpaRepositoryProvider.class,
		})
public class ComponentConfiguration {

}
