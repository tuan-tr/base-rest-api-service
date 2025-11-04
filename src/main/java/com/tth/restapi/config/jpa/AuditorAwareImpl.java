package com.tth.restapi.config.jpa;

import com.tth.common.util.AppConstant;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Optional;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		// ThreadContext.get(AppConstant.USERNAME);
		return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
				.map(e -> (String) e.getAttribute(AppConstant.USERNAME, RequestAttributes.SCOPE_REQUEST));
	}

}
