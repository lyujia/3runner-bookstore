package com.nhnacademy.bookstore.global.keymanager.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class KeyManagerResponse {
	private Header header;
	private Body body;

	@Getter
	@NoArgsConstructor
	public static class Body {
		private String secret;
	}

	@Getter
	@NoArgsConstructor
	public static class Header {
		private Integer resultCode;
		private String resultMessage;
		private boolean isSuccessful;
	}
}
