package com.nhnacademy.bookstore.category.repository;

import com.nhnacademy.bookstore.book.category.dto.response.CategoryParentWithChildrenResponse;
import com.nhnacademy.bookstore.book.category.dto.response.CategoryResponse;
import com.nhnacademy.bookstore.book.category.repository.CategoryRepository;
import com.nhnacademy.bookstore.book.category.repository.impl.CategoryCustomRepositoryImpl;
import com.nhnacademy.bookstore.entity.category.Category;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@DataJpaTest
@Import(CategoryCustomRepositoryImpl.class)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    private JPAQueryFactory queryFactory;

    private List<Category> categoryList;

    @BeforeEach
    void setUp() {
        queryFactory = new JPAQueryFactory(entityManager);

        // 최상위 부모 카테고리 생성
        Category grandParentCategory = new Category();
        grandParentCategory.setName("최상위 부모 카테고리");

        // 2번째 계층 카테고리 생성
        Category parentCategory1 = new Category();
        parentCategory1.setName("부모 카테고리1");
        parentCategory1.setParent(grandParentCategory);

        Category parentCategory2 = new Category();
        parentCategory2.setName("부모 카테고리2");
        parentCategory2.setParent(grandParentCategory);

        // 3번째 계층 카테고리 생성
        Category childCategory1 = new Category();
        childCategory1.setName("자식 카테고리1");
        childCategory1.setParent(parentCategory1);

        Category childCategory2 = new Category();
        childCategory2.setName("자식 카테고리2");
        childCategory2.setParent(parentCategory2);

        // 계층 관계 설정
        grandParentCategory.setChildren(new ArrayList<>(List.of(parentCategory1, parentCategory2)));
        parentCategory1.setChildren(new ArrayList<>(List.of(childCategory1)));
        parentCategory2.setChildren(new ArrayList<>(List.of(childCategory2)));

        this.categoryList = new ArrayList<>(
                List.of(grandParentCategory, parentCategory1, parentCategory2, childCategory1, childCategory2));
    }

    @DisplayName("카테고리 저장 테스트")
    @Test
    void saveParentCategoryTest() {
        Category category = new Category();
        category.setName("카테고리");

        categoryRepository.save(category);
        Optional<Category> savedCategory = categoryRepository.findById(category.getId());

        Assertions.assertTrue(savedCategory.isPresent());
        Assertions.assertEquals("카테고리", savedCategory.get().getName());
        Assertions.assertNull(savedCategory.get().getParent());
    }

    @DisplayName("자식 카테고리 저장 테스트")
    @Test
    void saveChildCategoryTest() {
        Category parent = new Category();
        parent.setName("부모 카테고리");

        Category child = new Category();
        child.setName("자식 카테고리");
        child.setParent(parent);

        categoryRepository.save(parent);
        categoryRepository.save(child);
        parent.addChildren(child);

        Optional<Category> foundParent = categoryRepository.findById(parent.getId());
        Optional<Category> foundChild = categoryRepository.findById(child.getId());

        Assertions.assertTrue(foundParent.isPresent());
        Assertions.assertTrue(foundChild.isPresent());
        log.info("부모 카테고리 : {}", foundParent.get());
        log.info("자식 카테고리 : {}", foundChild.get());

        Assertions.assertEquals("부모 카테고리", foundParent.get().getName());
        Assertions.assertEquals("자식 카테고리", foundChild.get().getName());
        Assertions.assertEquals(parent, foundChild.get().getParent());

        // 부모-자식이 제대로 설정됐는지 확인
        Assertions.assertTrue(foundParent.get().getChildren().stream()
                .anyMatch(c -> c.getId() == foundChild.get().getId()));
    }

    @DisplayName("카테고리 이름 조회 테스트")
    @Test
    void findByCategoryNameTest() {
        Category category = new Category();
        category.setName("카테고리");

        categoryRepository.save(category);
        Optional<Category> result = categoryRepository.findByName("카테고리");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(category.getName(), result.get().getName());
    }

    @DisplayName("모든 카테고리 조회 테스트")
    @Test
    void findCategoriesTest() {
        categoryRepository.saveAll(categoryList);

        List<CategoryResponse> categories = categoryRepository.findCategories();
        log.info("저장된 카테고리 목록 : {}", categories);

        Assertions.assertNotNull(categories);
        Assertions.assertEquals(5, categories.size());
    }

    @DisplayName("최상위 카테고리 조회 테스트")
    @Test
    void findAllParentCategoriesTest() {
        categoryRepository.saveAll(categoryList);
        log.info("size = {}", categoryList.size()); // size = 5
        log.info("{}", categoryList);

        List<CategoryResponse> parentCategories = categoryRepository.findTopCategories();
        log.info("size = {}", parentCategories.size());

        Assertions.assertEquals(1, parentCategories.size());
    }

    @DisplayName("계층 카테고리 조회 테스트")
    @Test
    void findParentWithChildrenCategoriesTest() {
        categoryRepository.saveAll(categoryList);
        List<CategoryParentWithChildrenResponse> parentWithChildrenCategories = categoryRepository.findParentWithChildrenCategories();

        Assertions.assertEquals(1, parentWithChildrenCategories.size());

        CategoryParentWithChildrenResponse grandParentResponse = parentWithChildrenCategories.get(0);
        Assertions.assertEquals("최상위 부모 카테고리", grandParentResponse.getName());
        Assertions.assertNotNull(grandParentResponse.getChildrenList());
        Assertions.assertEquals(2, grandParentResponse.getChildrenList().size());

        CategoryParentWithChildrenResponse parentResponse1 = grandParentResponse.getChildrenList().get(0);
        Assertions.assertEquals("부모 카테고리1", parentResponse1.getName());
        Assertions.assertNotNull(parentResponse1.getChildrenList());
        Assertions.assertEquals(1, parentResponse1.getChildrenList().size());

        CategoryParentWithChildrenResponse parentResponse2 = grandParentResponse.getChildrenList().get(1);
        Assertions.assertEquals("부모 카테고리2", parentResponse2.getName());
        Assertions.assertNotNull(parentResponse2.getChildrenList());
        Assertions.assertEquals(1, parentResponse2.getChildrenList().size());

        CategoryParentWithChildrenResponse childResponse1 = parentResponse1.getChildrenList().get(0);
        Assertions.assertEquals("자식 카테고리1", childResponse1.getName());
        Assertions.assertNotNull(childResponse1.getChildrenList());
        Assertions.assertTrue(childResponse1.getChildrenList().isEmpty());

        CategoryParentWithChildrenResponse childResponse2 = parentResponse2.getChildrenList().get(0);
        Assertions.assertEquals("자식 카테고리2", childResponse2.getName());
        Assertions.assertNotNull(childResponse2.getChildrenList());
        Assertions.assertTrue(childResponse2.getChildrenList().isEmpty());
    }
}
