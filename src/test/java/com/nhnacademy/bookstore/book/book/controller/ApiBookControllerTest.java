package com.nhnacademy.bookstore.book.book.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.nhnacademy.bookstore.book.book.service.ApiBookService;

@ExtendWith(MockitoExtension.class)
class ApiBookControllerTest {

	@Mock
	private ApiBookService apiBookService;

	@InjectMocks
	private ApiBookController apiBookController;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(apiBookController).build();
	}

	@Test
	void testBooks() throws Exception {
		// Given
		doNothing().when(apiBookService).save(anyString());

		// When & Then
		mockMvc.perform(get("/bookstore/api/books/1234567890123"))
			.andExpect(status().is2xxSuccessful());

		verify(apiBookService).save(anyString());
	}
}
