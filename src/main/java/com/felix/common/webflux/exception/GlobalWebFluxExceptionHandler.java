package com.felix.common.webflux.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import com.felix.common.exception.ApplicationException;
import com.felix.common.exception.BusinessException;
import com.felix.common.webflux.common.WebFluxResponseResult;
import com.felix.common.webflux.util.MessageSourceUtils;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalWebFluxExceptionHandler {

	@Autowired
	MessageSourceUtils messageSourceUtils;

	private static final Logger logger = LoggerFactory.getLogger(GlobalWebFluxExceptionHandler.class);

	@ExceptionHandler(BusinessException.class)
	public Mono<WebFluxResponseResult> catchBusinessException(ServerWebExchange exchange, BusinessException ex) {
		String message = "";
		if (null == ex.getObjects()) {
			message = messageSourceUtils.getMessage(exchange, String.valueOf(ex.getErrorCode()));
		} else {
			message = messageSourceUtils.getMessage(exchange, String.valueOf(ex.getErrorCode()), ex.getObjects());
		}
		logger.error(ex.getMessage(), ex);
		return WebFluxResponseResult.error(ex.getErrorCode(), ex.getHttpStatus(), message);
	}

	@ExceptionHandler(ApplicationException.class)
	public Mono<WebFluxResponseResult> catchApplicationException(ServerWebExchange exchange, ApplicationException ex) {
		String message = "";
		if (null == ex.getObjects()) {
			message = messageSourceUtils.getMessage(exchange, String.valueOf(ex.getErrorCode()));
		} else {
			message = messageSourceUtils.getMessage(exchange, String.valueOf(ex.getErrorCode()), ex.getObjects());
		}
		logger.error(ex.getMessage(), ex);
		return WebFluxResponseResult.error(ex.getErrorCode(), ex.getHttpStatus(), message);
	}

	@ExceptionHandler(Exception.class)
	public Mono<WebFluxResponseResult> catchException(ServerWebExchange webExchange, Exception ex) {
		logger.error(ex.getMessage(), ex);
		return WebFluxResponseResult.error(-1, 500, ex.getMessage());
	}
}
