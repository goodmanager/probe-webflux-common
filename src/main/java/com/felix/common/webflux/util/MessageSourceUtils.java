package com.felix.common.webflux.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.felix.common.webflux.config.LocaleResolver;

/**
 * 
 * @author felix
 *
 */
@Component
public class MessageSourceUtils {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private LocaleResolver localeResolver;

	public String getMessage(ServerWebExchange exchange, String key, Object... params) {
		LocaleContext localeContext = localeResolver.resolveLocaleContext(exchange);
		return messageSource.getMessage(key, params, localeContext.getLocale());
	}
}
