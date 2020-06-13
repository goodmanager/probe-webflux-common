package com.felix.common.webflux.log;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcHeaderFilter implements WebFilter {
	private static final String MDC_HEADER = "X-MDC-TrackId";
	public static final String CONTEXT_MAP = "context-map";

	@Override
	public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain chain) {
		serverWebExchange.getResponse()
				.beforeCommit(() -> addContextToHttpResponseHeaders(serverWebExchange.getResponse()));

		return chain.filter(serverWebExchange)
				.subscriberContext(ctx -> addRequestHeadersToContext(serverWebExchange.getRequest(), ctx));
	}

	private Context addRequestHeadersToContext(final ServerHttpRequest request, final Context context) {
		final Map<String, String> contextMap = request.getHeaders().toSingleValueMap().entrySet().stream()
				.filter(x -> x.getKey().equals(MDC_HEADER))
				.collect(toMap(key -> MDC_HEADER, val -> val.setValue(UUID.randomUUID().toString())));

		return context.put(CONTEXT_MAP, contextMap);
	}

	private Mono<Void> addContextToHttpResponseHeaders(final ServerHttpResponse serverHttpResponse) {
		return Mono.subscriberContext().doOnNext(ctx -> {
			if (!ctx.hasKey(CONTEXT_MAP)) {
				return;
			}
			final HttpHeaders headers = serverHttpResponse.getHeaders();
			ctx.<Map<String, String>>get(CONTEXT_MAP).forEach((key, value) -> headers.add(key, value));
		}).then();
	}
}
