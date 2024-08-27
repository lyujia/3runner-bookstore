package com.nhnacademy.bookstore.book.tag.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.bookstore.entity.tag.Tag;

/**
 * 태그 CRUD jpa
 * @author 정주혁
 */
public interface TagRepository extends JpaRepository<Tag, Long>, TagCustomRepository {
	Optional<Tag> findByName(String name);
}
