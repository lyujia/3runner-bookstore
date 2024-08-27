package com.nhnacademy.bookstore.global.elastic.document.book;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(indexName = "3runner_book_alias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDocument {
	@Id
	@Field(type = FieldType.Keyword)
	private long id;

	@Field(type = FieldType.Text, copyTo = {"keywordText", "titleNgram"}, analyzer = "nori_analyzer")
	private String title;

	@Field(type = FieldType.Text, analyzer = "ngram_analyzer")
	private String titleNgram;

	@Field(type = FieldType.Text, copyTo = {"keywordList", "keywordText"}, analyzer = "ngram_analyzer")
	private String author;

	@Field(type = FieldType.Keyword)
	private String thumbnail;

	@Field(type = FieldType.Text, copyTo = {"keywordList", "keywordText"}, analyzer = "whitespace_analyzer")
	private String publisher;

	@Field(type = FieldType.Keyword)
	int price;

	@Field(type = FieldType.Keyword)
	int sellingPrice;

	@Field(type = FieldType.Text, copyTo = {"keywordList", "keywordText"}, analyzer = "nori_analyzer")
	private List<String> tagList;

	@Field(type = FieldType.Keyword, copyTo = {"keywordList", "keywordText"})
	private List<String> categoryList;

	@Field(type = FieldType.Text, analyzer = "nori_analyzer")
	private List<String> keywordText;

	@Field(type = FieldType.Text)
	private List<String> keywordList;

	public BookDocument(long id, String title, String author, String thumbnail, String publisher,
		List<String> tagList, List<String> categoryList, int price, int sellingPrice) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.thumbnail = thumbnail;
		this.publisher = publisher;
		this.tagList = tagList;
		this.categoryList = categoryList;
		this.price = price;
		this.sellingPrice = sellingPrice;
	}
}