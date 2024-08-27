package com.nhnacademy.bookstore.global.appender.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 한민기
 * 일단 필수만 추가했음
 */
@Setter
@Getter
public class LogCrashRequest {
	String projectName;
	String projectVersion;
	String logVersion;
	String body;
	String sendTime;
	String logSource;
	String logType;
	String host;

	private static final String DEFAULT_PROJECT_NAME = "Xyx7DoyszcG66ULx";
	private static final String DEFAULT_PROJECT_VERSION = "1.0.0";
	private static final String DEFAULT_LOG_VERSION = "v2";
	private static final String DEFAULT_LOG_SOURCE = "http";
	private static final String DEFAULT_LOG_TYPE = "nelo2-http";
	private static final String DEFAULT_HOST = "3runner-bookstore";

	public LogCrashRequest(String body) {
		this.body = body;
		this.projectName = DEFAULT_PROJECT_NAME;
		this.projectVersion = DEFAULT_PROJECT_VERSION;
		this.logVersion = DEFAULT_LOG_VERSION;
		this.logSource = DEFAULT_LOG_SOURCE;
		this.logType = DEFAULT_LOG_TYPE;
		this.host = DEFAULT_HOST;
	}
}
