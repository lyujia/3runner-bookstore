package com.nhnacademy.bookstore.entity.category;

import java.util.ArrayList;
import java.util.List;

import com.nhnacademy.bookstore.entity.bookcategory.BookCategory;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter
	private long id;

	@NotNull
	@Size(min = 1, max = 30)
	@Setter
	private String name;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Category parent;

	// 연결
	@Setter
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
	private List<Category> children = new ArrayList<>();

	@Setter
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BookCategory> bookCategoryList = new ArrayList<>();

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public void addChildren(Category child) {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		child.setParent(this);
		this.children.add(child);
	}

	public Category(String name) {
		this.name = name;
	}
}
