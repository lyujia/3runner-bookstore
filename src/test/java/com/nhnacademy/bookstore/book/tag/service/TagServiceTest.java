package com.nhnacademy.bookstore.book.tag.service.Impl;

import com.nhnacademy.bookstore.book.tag.dto.request.CreateTagRequest;
import com.nhnacademy.bookstore.book.tag.dto.request.DeleteTagRequest;
import com.nhnacademy.bookstore.book.tag.dto.request.UpdateTagRequest;
import com.nhnacademy.bookstore.book.tag.dto.response.TagResponse;
import com.nhnacademy.bookstore.book.tag.exception.AlreadyHaveTagException;
import com.nhnacademy.bookstore.book.tag.exception.NotExistsTagException;
import com.nhnacademy.bookstore.book.tag.repository.TagRepository;
import com.nhnacademy.bookstore.entity.tag.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTags() {
        List<Tag> tags = new ArrayList<>();
        Tag tag1 = new Tag();
        tag1.setName("Tag1");
        tags.add(tag1);

        when(tagRepository.findAll()).thenReturn(tags);

        List<TagResponse> result = tagService.getAllTags();
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Tag1");
    }

    @Test
    void testCreateTag() {
        CreateTagRequest request = new CreateTagRequest("NewTag");
        Tag tag = new Tag();
        tag.setName("NewTag");

        when(tagRepository.findByName(any())).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Long result = tagService.createTag(request);
        assertThat(result).isEqualTo(0L);
    }

    @Test
    void testCreateTag_AlreadyExists() {
        CreateTagRequest request = new CreateTagRequest("ExistingTag");

        when(tagRepository.findByName(any())).thenReturn(Optional.of(new Tag()));

        assertThrows(AlreadyHaveTagException.class, () -> tagService.createTag(request));
    }

    @Test
    void testDeleteTag() {
        DeleteTagRequest request = new DeleteTagRequest(1L);

        when(tagRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(tagRepository).deleteById(anyLong());

        tagService.deleteTag(request);

        verify(tagRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTag_NotExists() {
        DeleteTagRequest request = new DeleteTagRequest(1L);

        when(tagRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotExistsTagException.class, () -> tagService.deleteTag(request));
    }

    @Test
    void testUpdateTag() {
        UpdateTagRequest request = new UpdateTagRequest(1L, "UpdatedTag");
        Tag tag = new Tag();
        tag.setName("OldTag");

        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Long result = tagService.updateTag(request);
        assertThat(result).isEqualTo(0L);
        assertThat(tag.getName()).isEqualTo("UpdatedTag");
    }

    @Test
    void testUpdateTag_NotExists() {
        UpdateTagRequest request = new UpdateTagRequest(1L, "UpdatedTag");

        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotExistsTagException.class, () -> tagService.updateTag(request));
    }

    @Test
    void testGetAllAdminTags() {
        Pageable pageable = PageRequest.of(0, 10);
        List<TagResponse> tagResponses = new ArrayList<>();
        TagResponse response = TagResponse.builder().id(1L).name("Tag1").build();
        tagResponses.add(response);
        Page<TagResponse> page = new PageImpl<>(tagResponses, pageable, 1);

        when(tagRepository.readAdminBookList(any(Pageable.class))).thenReturn(page);

        Page<TagResponse> result = tagService.getAllAdminTags(pageable);
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo("Tag1");
    }
}
