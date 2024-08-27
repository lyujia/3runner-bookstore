package com.nhnacademy.bookstore.book.category.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.bookstore.entity.category.Category;

/**
 * category repository
 * @author 김은비
 */
public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryCustomRepository {
	boolean existsByName(String name);

	Optional<Category> findByName(String name);

}
