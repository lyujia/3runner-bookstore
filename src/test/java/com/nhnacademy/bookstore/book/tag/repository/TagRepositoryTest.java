package com.nhnacademy.bookstore.book.tag.repository;

import com.nhnacademy.bookstore.book.tag.dto.response.TagResponse;
import com.nhnacademy.bookstore.book.tag.repository.impl.TagCustomRepositoryImpl;
import com.nhnacademy.bookstore.entity.tag.QTag;
import com.nhnacademy.bookstore.entity.tag.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(TagCustomRepositoryImpl.class)
class TagRepositoryTest {

    @Qualifier("tagCustomRepositoryImpl")
    @Autowired
    private TagCustomRepository tagCustomRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final QTag qTag = QTag.tag;

    @BeforeEach
    void setUp() {
        Tag tag1 = new Tag();
        tag1.setName("Tag1");
        entityManager.persist(tag1);

        Tag tag2 = new Tag();
        tag2.setName("Tag2");
        entityManager.persist(tag2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testReadAdminBookList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TagResponse> tagResponses = tagCustomRepository.readAdminBookList(pageable);

        assertThat(tagResponses).isNotNull();
        assertThat(tagResponses.getTotalElements()).isEqualTo(2);
        assertThat(tagResponses.getContent()).hasSize(2);
        List<TagResponse> content = tagResponses.getContent();
        assertThat(content.get(0).name()).isEqualTo("Tag1");
        assertThat(content.get(1).name()).isEqualTo("Tag2");
    }
}
