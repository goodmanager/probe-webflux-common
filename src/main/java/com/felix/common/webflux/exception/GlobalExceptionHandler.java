package com.felix.common.webflux.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import com.felix.common.exception.ApplicationException;
import com.felix.common.exception.BusinessException;
import com.felix.common.response.ExceptionResponseResult;
import com.felix.common.util.ResponseUtil;
import com.felix.common.webflux.util.MessageSourceUtils;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@Autowired
	MessageSourceUtils messageSourceUtils;

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(BusinessException.class)
	public ExceptionResponseResult<String> catchBusinessException(ServerWebExchange exchange, BusinessException ex) {
		if (null == ex.getObjects()) {
			ex.setMessage(messageSourceUtils.getMessage(exchange, String.valueOf(ex.getErrorCode())));
		} else {
			ex.setMessage(messageSourceUtils.getMessage(exchange, String.valueOf(ex.getErrorCode()), ex.getObjects()));
		}
		logger.error(ex.getMessage(), ex);
		return ResponseUtil.createExceptionResponseResult(ex.getErrorCode(), ex.getHttpStatus(), ex.getMessage());
	}

	@ExceptionHandler(ApplicationException.class)
	public ExceptionResponseResult<String> catchApplicationException(ServerWebExchange exchange,
			ApplicationException ex) {
		if (null == ex.getObjects()) {
			ex.setMessage(messageSourceUtils.getMessage(exchange, String.valueOf(ex.getErrorCode())));
		} else {
			ex.setMessage(messageSourceUtils.getMessage(exchange, String.valueOf(ex.getErrorCode()), ex.getObjects()));
		}
		logger.error(ex.getMessage(), ex);
		return ResponseUtil.createExceptionResponseResult(ex.getErrorCode(), ex.getHttpStatus(), ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ExceptionResponseResult<String> catchException(ServerWebExchange webExchange, Exception ex) {
		logger.error(ex.getMessage(), ex);
		return ResponseUtil.createExceptionResponseResult(-1, 500, ex.getMessage());
	}
}
