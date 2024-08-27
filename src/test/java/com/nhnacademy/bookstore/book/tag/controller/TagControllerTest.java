package com.nhnacademy.bookstore.book.tag.controller;

import com.nhnacademy.bookstore.book.tag.dto.request.CreateTagRequest;
import com.nhnacademy.bookstore.book.tag.dto.request.DeleteTagRequest;
import com.nhnacademy.bookstore.book.tag.dto.request.UpdateTagRequest;
import com.nhnacademy.bookstore.book.tag.dto.response.TagResponse;
import com.nhnacademy.bookstore.book.tag.exception.NotExistsTagException;
import com.nhnacademy.bookstore.book.tag.service.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @DisplayName("모든 태그 가져오기 테스트")
    @Test
    void getAllTags() throws Exception {
        List<TagResponse> tags = Collections.emptyList();
        when(tagService.getAllTags()).thenReturn(tags);

        mockMvc.perform(get("/bookstore/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.header.resultCode").value(200))
                .andExpect(jsonPath("$.header.successful").value(true))
                .andExpect(jsonPath("$.body.data").isArray())
                .andDo(print());

        verify(tagService).getAllTags();
    }

    @DisplayName("태그 생성 테스트")
    @Test
    void createTag() throws Exception {
        CreateTagRequest request = new CreateTagRequest("New Tag");

        mockMvc.perform(post("/bookstore/tags")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "New Tag"))
                .andExpect(jsonPath("$.header.successful").value(true))
                .andDo(print());

        verify(tagService).createTag(any(CreateTagRequest.class));
    }


    @DisplayName("태그 삭제 테스트")
    @Test
    void deleteTag() throws Exception {
        DeleteTagRequest request = new DeleteTagRequest(1L);

        mockMvc.perform(delete("/bookstore/tags")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("tagId", "1"))
                .andExpect(jsonPath("$.header.successful").value(true))
                .andDo(print());

        verify(tagService).deleteTag(any(DeleteTagRequest.class));
    }

    @DisplayName("태그 업데이트 - 태그가 존재하지 않는 경우")
    @Test
    void updateTag_NotExists() throws Exception {
        UpdateTagRequest request = new UpdateTagRequest(1L, "Updated Tag");

        when(tagService.updateTag(any(UpdateTagRequest.class))).thenThrow(new NotExistsTagException(""));

        mockMvc.perform(put("/bookstore/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tagId\":1,\"tagName\":\"Updated Tag\"}"))
                .andExpect(jsonPath("$.header.resultCode").value(500))
                .andExpect(jsonPath("$.header.successful").value(false))
                .andDo(print());

        verify(tagService).updateTag(any(UpdateTagRequest.class));
    }

    @DisplayName("관리자 페이지에서 태그 목록 불러오기 테스트")
    @Test
    void readAllAdminTags() throws Exception {
        Page<TagResponse> tags = Page.empty();
        when(tagService.getAllAdminTags(any(Pageable.class))).thenReturn(tags);

        mockMvc.perform(get("/bookstore/tags/admin")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.header.resultCode").value(200))
                .andExpect(jsonPath("$.header.successful").value(true))
                .andExpect(jsonPath("$.body.data.content").isArray())
                .andDo(print());

        verify(tagService).getAllAdminTags(any(Pageable.class));
    }
}
