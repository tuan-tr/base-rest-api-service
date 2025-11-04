package com.tth.restapi.config;

import com.tth.common.auth.AuthEnviroment;
import com.tth.restapi.config.auth.AppBearerTokenAccessDeniedHandler;
import com.tth.restapi.config.auth.AppBearerTokenAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class AuthConfiguration {

	private final AuthEnviroment authEnv;
	private final AppBearerTokenAuthenticationEntryPoint appBearerTokenAuthenticationEntryPoint;
	private final AppBearerTokenAccessDeniedHandler appBearerTokenAccessDeniedHandler;

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
		corsConfiguration.setAllowedOrigins(Arrays.asList(authEnv.getCorsAllowedOrigins()));
		corsConfiguration.addAllowedMethod(HttpMethod.PUT);
		corsConfiguration.addAllowedMethod(HttpMethod.DELETE);
		corsConfiguration.addAllowedMethod(HttpMethod.OPTIONS);
		corsConfiguration.addExposedHeader(HttpHeaders.CONTENT_DISPOSITION);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.cors(Customizer.withDefaults())
				.csrf(customizer -> customizer.disable())
				.httpBasic(customizer -> customizer.disable())
				.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(authEnv.getNonAuthenticatedPaths()).permitAll()
						.anyRequest().authenticated()
				)
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(Customizer.withDefaults())
						.authenticationEntryPoint(appBearerTokenAuthenticationEntryPoint)
						.accessDeniedHandler(appBearerTokenAccessDeniedHandler)
				)
		;
		return http.build();
	}

}
