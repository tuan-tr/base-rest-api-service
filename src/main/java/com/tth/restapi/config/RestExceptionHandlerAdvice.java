package com.tth.restapi.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tth.common.exception.BadBusinessException;
import com.tth.common.exception.DataNotFoundException;
import com.tth.common.exception.UnsupportedSortPropertyException;
import com.tth.common.http.FailureResponseBody;
import com.tth.common.i18n.Translator;
import com.tth.common.util.AppExceptionUtils;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class RestExceptionHandlerAdvice {

	private static final String REQUIRED_BODY_MESSAGE = "Required request body is missing";
	private static final String ASSERT_TRUE = "AssertTrue";

	private final Translator translator;

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BadBusinessException.class)
	FailureResponseBody handle(BadBusinessException ex, WebRequest request) {
		String message = translator.toLocale(ex.getCode(), ex.getDetails());
		return new FailureResponseBody(ex.getCode(), message, ex.getDetails());
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(DataNotFoundException.class)
	@Hidden
	FailureResponseBody handle(DataNotFoundException ex, WebRequest request) {
		String message = translator.toLocale(ex.getCode(), ex.getDetails());
		return new FailureResponseBody(ex.getCode(), message, ex.getDetails());
	}

	@ExceptionHandler(value = {
		NoHandlerFoundException.class,
		NoResourceFoundException.class
	})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@Hidden
	FailureResponseBody notFoundHandle(Exception ex, WebRequest request) {
		return generalHandle(ex);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@Hidden
	FailureResponseBody handle(HttpRequestMethodNotSupportedException ex, WebRequest request) {
		return generalHandle(ex);
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@Hidden
	FailureResponseBody handle(AccessDeniedException ex, WebRequest request) {
		return generalHandle(ex);
	}

	@ExceptionHandler(value = {
		MissingServletRequestParameterException.class,
		MissingServletRequestPartException.class,
		HttpMediaTypeNotSupportedException.class,
		UnsupportedSortPropertyException.class,
		ConversionFailedException.class,
		PropertyReferenceException.class,
		MultipartException.class,
	})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	FailureResponseBody handle(Exception ex, WebRequest request) {
		return generalHandle(ex);
	}

	private FailureResponseBody generalHandle(Exception ex) {
		String code = AppExceptionUtils.extractErrorCode(ex);
		return new FailureResponseBody(code, ex.getMessage());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	FailureResponseBody handle(HttpMessageNotReadableException ex, WebRequest request) {
		Throwable cause = ex.getMostSpecificCause();
		String code = AppExceptionUtils.extractErrorCode(cause);
		String message = cause.getLocalizedMessage();
		if (cause instanceof JsonProcessingException) {
			message = ((JsonProcessingException) cause).getOriginalMessage();
		} else if (message.startsWith(REQUIRED_BODY_MESSAGE)) {
			message = REQUIRED_BODY_MESSAGE;
		}

		FailureResponseBody response = new FailureResponseBody(code, message);
		return response;
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	FailureResponseBody handle(MethodArgumentTypeMismatchException ex, WebRequest request) {
		Throwable cause = ex.getMostSpecificCause();
		String code = AppExceptionUtils.extractErrorCode(cause);
		String message = cause.getLocalizedMessage();
		FailureResponseBody response = new FailureResponseBody(code, message);
		return response;
	}

	@ExceptionHandler(BindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	FailureResponseBody handle(BindException ex, WebRequest request) {
		String code = AppExceptionUtils.extractErrorCode(ex);
		String messages = ex.getFieldErrors().stream()
				.map(e -> {
					boolean needToTranslate = e.getDefaultMessage().contains(StringUtils.SPACE) == false;
					if (needToTranslate) {
						return translator.toLocale(e.getDefaultMessage());
					}
					if (ASSERT_TRUE.equals(e.getCode())) {
						return e.getDefaultMessage();
					}
					return String.format("%s %s", e.getField(), e.getDefaultMessage());
				})
				.collect(Collectors.joining("\n"));

		FailureResponseBody response = new FailureResponseBody(code, messages, ex.getFieldErrors());
		return response;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	FailureResponseBody remainingHandle(Exception ex, WebRequest request) {
		log.error(ExceptionUtils.getStackTrace(ex));
		String code = HttpStatus.INTERNAL_SERVER_ERROR.name();
		String message = translator.toLocale(code);
		return new FailureResponseBody(code, message);
	}

}
