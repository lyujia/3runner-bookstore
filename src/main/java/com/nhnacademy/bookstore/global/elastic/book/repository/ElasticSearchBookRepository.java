package com.nhnacademy.bookstore.global.elastic.book.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.nhnacademy.bookstore.global.elastic.document.book.BookDocument;

public interface ElasticSearchBookRepository extends ElasticsearchRepository<BookDocument, Long> {

	List<BookDocument> findByTitle(String title);

	@Query("{"
		+ "    \"function_score\": {"
		+ "      \"query\": {"
		+ "        \"multi_match\": {"
		+ "          \"query\": \"?0\", "
		+ "          \"minimum_should_match\": \"70%\","
		+ "          \"fields\": [\"title^70\", \"titleNgram^70\", \"author^50\", \"publisher^50\", \"categoryList^50\", \"tagList^60\"]"
		+ "        }"
		+ "      }"
		+ "    }"
		+ "  }")
	Page<BookDocument> findByCustomQuery(String query, Pageable pageable);
}
