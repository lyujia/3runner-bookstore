package com.nhnacademy.bookstore.entity.tag;

import java.util.ArrayList;
import java.util.List;

import com.nhnacademy.bookstore.entity.booktag.BookTag;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, unique = true)
	@Size(min = 1, max = 100)
	@Setter
	private String name;

	public Tag(String name) {
		this.name = name;
	}
	//연결

	@OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BookTag> bookTagList = new ArrayList<>();

}
