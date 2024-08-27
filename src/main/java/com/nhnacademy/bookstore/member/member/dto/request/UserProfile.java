package com.nhnacademy.bookstore.member.member.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserProfile {
	@JsonProperty("id")
	private String id;
	@JsonProperty("email")
	private String email;
	@JsonProperty("mobile")
	private String mobile;
	@JsonProperty("name")
	private String name;

	// 생성자
	public UserProfile(JsonNode jsonNode) {
		JsonNode memberNode = jsonNode.path("data").path("member");
		this.id = memberNode.path("id").asText();
		this.email = memberNode.path("email").asText();
		this.name = memberNode.path("name").asText();
		this.mobile = memberNode.path("mobile").asText();
	}

	// 생성자
	public UserProfile(String id, String email, String mobile, String name) {
		this.id = id;
		this.email = email;
		this.mobile = mobile;
		this.name = name;
	}
}
