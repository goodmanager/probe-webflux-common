package com.felix.common.webflux.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.felix.common.response.Pager;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

public class WebFluxResponseResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5222538666507454852L;

	private int errorCode;

	private int httpStatus;

	private String message;

	private Pager pager;

	private Object data;

	public static Mono<WebFluxResponseResult> success(Mono<Pager> pager, Flux<?> list) {
		WebFluxResponseResult result = new WebFluxResponseResult();

		return Mono.create(sink -> {
			pager.subscribe(new BaseSubscriber<Pager>() {
				List<Pager> pagers = new ArrayList<>(1);

				@Override
				public void hookOnNext(Pager ob) {
					pagers.add(ob);
				}

				@Override
				public void hookOnComplete() {
					result.setPager(pagers.get(0));
					dispose();
				}
			});
			list.subscribe(buildReturn(sink, result));
		});
	}

	public static Mono<WebFluxResponseResult> success(Mono<?> object) {
		WebFluxResponseResult result = new WebFluxResponseResult();
		return Mono.create(sink -> {
			object.subscribe(buildReturn(sink, result));
		});
	}

	public static Mono<WebFluxResponseResult> error(int errorCode, int httpStatus, String message) {
		WebFluxResponseResult result = new WebFluxResponseResult();
		return Mono.create(sink -> {
			result.setMessage(message);
			result.setErrorCode(errorCode);
			result.setHttpStatus(httpStatus);
			sink.success(result);
		});
	}

	private static BaseSubscriber<Object> buildReturn(MonoSink<WebFluxResponseResult> sink,
			WebFluxResponseResult result) {
		return new BaseSubscriber<Object>() {
			List<Object> objects = new ArrayList<>();

			@Override
			public void hookOnNext(Object ob) {
				objects.add(ob);
			}

			@Override
			public void hookOnComplete() {
				result.setData(objects);
				result.setMessage("");
				result.setErrorCode(0);
				result.setHttpStatus(200);
				sink.success(result);
			}
		};
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Pager getPager() {
		return pager;
	}

	public void setPager(Pager pager) {
		this.pager = pager;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}