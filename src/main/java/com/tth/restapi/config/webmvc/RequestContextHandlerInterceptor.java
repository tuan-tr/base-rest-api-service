package com.tth.restapi.config.webmvc;

import com.tth.common.util.AppConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class RequestContextHandlerInterceptor implements HandlerInterceptor {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler
	) throws Exception {
		Jwt jwtPrincipal = Optional.ofNullable(SecurityContextHolder.getContext())
				.map(SecurityContext::getAuthentication)
				.filter(Authentication::isAuthenticated)
				.map(Authentication::getPrincipal)
				.filter(Jwt.class::isInstance)
				.map(Jwt.class::cast)
				.orElseThrow(() -> new AuthenticationCredentialsNotFoundException("No current user associated with this request"));
		
		String username = jwtPrincipal.getClaimAsString(AppConstant.USERNAME);
		request.setAttribute(AppConstant.USERNAME, username);
		ThreadContext.put(AppConstant.USERNAME, username);
		
		return true;
	}
	
}
