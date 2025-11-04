package com.tth.restapi.config.auth;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.tth.common.util.AppConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Component
@Log4j2
@RequiredArgsConstructor
public class AppJwtDecoder implements JwtDecoder {

	private static final String DECODING_ERROR_MESSAGE_TEMPLATE = "An error occurred while attempting to decode the Jwt: %s";
	private static final String INVALID_TOKEN = "Invalid Token";

	@Override
	public Jwt decode(String token) throws JwtException {
		try {
			JWT JWT = JWTParser.parse(token);
			Map<String, Object> claims = JWT.getJWTClaimsSet().toJSONObject();
			if (claims.get(AppConstant.USERNAME) == null) {
				throw new BadJwtException(INVALID_TOKEN);
			}
			
			Instant issueInstant = Optional.ofNullable(JWT.getJWTClaimsSet().getIssueTime()).map(e -> e.toInstant()).orElse(null);
			Instant expirationInstant = Optional.ofNullable(JWT.getJWTClaimsSet().getExpirationTime()).map(e -> e.toInstant()).orElse(null);
			
			// claims.put("scope", scopes);
			// claims.put(AppConstant.USERNAME, "username");
			
			return new Jwt(token, issueInstant, expirationInstant, JWT.getHeader().toJSONObject(), claims);
		} catch (ParseException ex) {
			log.error(ex.getMessage());
			throw new BadJwtException(String.format(DECODING_ERROR_MESSAGE_TEMPLATE, ex.getMessage()), ex);
		}
	}

}
