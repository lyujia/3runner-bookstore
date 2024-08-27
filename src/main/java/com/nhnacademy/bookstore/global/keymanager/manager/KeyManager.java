package com.nhnacademy.bookstore.global.keymanager.manager;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.nhnacademy.bookstore.global.keymanager.response.KeyManagerResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KeyManager {

	/**
	 * 키를 -> nhn key manager 로 보냄 -> RestTemplate 으로 보내서 값을 얻어옴.
	 *
	 * @param key 가져올 키
	 * @return 키 값을 통해 가져올 값
	 */
	public String getValue(String key) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-TC-AUTHENTICATION-ID", "PBobawJDVTpWdJr503pD");
		headers.add("X-TC-AUTHENTICATION-SECRET", "QBh3QJVcWeTzY2dr");
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		String url = "https://api-keymanager.nhncloudservice.com/keymanager/v1.2/appkey/";
		String apikey = "2SxwmBzUfnqJaA2A";
		String urlNext = "/secrets/";

		url = url + apikey + urlNext + key;

		KeyManagerResponse response = restTemplate.exchange(url, HttpMethod.GET, entity, KeyManagerResponse.class)
			.getBody();
		return response.getBody().getSecret();
	}
}
