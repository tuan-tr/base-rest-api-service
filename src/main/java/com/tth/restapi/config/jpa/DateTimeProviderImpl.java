package com.tth.restapi.config.jpa;

import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.stereotype.Component;

@Component("dateTimeProvider")
public class DateTimeProviderImpl implements DateTimeProvider {

	@Override
	public Optional<TemporalAccessor> getNow() {
		return Optional.of(OffsetDateTime.now());
	}

}
