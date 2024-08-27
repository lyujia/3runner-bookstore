package com.nhnacademy.bookstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Disabled
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public abstract class BaseDocumentTest {
	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected MockMvc mockMvc;

	protected final String snippetPath = "{class-name}/{method-name}";

	@BeforeEach
	void setUp(final WebApplicationContext context, final RestDocumentationContextProvider provider) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
			.apply(MockMvcRestDocumentation.documentationConfiguration(provider)
				//요청 body 의 payload 를 보기 좋게 출력
				.operationPreprocessors().withRequestDefaults(Preprocessors.prettyPrint())
				.and()
				//응답 body 의 payload 를 보기 좋게 출력
				.operationPreprocessors().withResponseDefaults(Preprocessors.prettyPrint()))
			//테스트 결과를 항상 print
			.alwaysDo(MockMvcResultHandlers.print())
			//한글 깨짐 방지
			.addFilter(new CharacterEncodingFilter("UTF-8", true))
			.build();
	}

	protected String createJson(Object dto) throws JsonProcessingException {
		return objectMapper.writeValueAsString(dto);
	}

	protected Attributes.Attribute attribute(final String key, final String value) {
		return new Attributes.Attribute(key, value);
	}
}