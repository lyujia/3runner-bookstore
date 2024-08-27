package com.nhnacademy.bookstore.book.tag.service.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.bookstore.book.tag.dto.request.CreateTagRequest;
import com.nhnacademy.bookstore.book.tag.dto.request.DeleteTagRequest;
import com.nhnacademy.bookstore.book.tag.dto.request.UpdateTagRequest;
import com.nhnacademy.bookstore.book.tag.dto.response.TagResponse;
import com.nhnacademy.bookstore.book.tag.exception.AlreadyHaveTagException;
import com.nhnacademy.bookstore.book.tag.exception.NotExistsTagException;
import com.nhnacademy.bookstore.book.tag.repository.TagRepository;
import com.nhnacademy.bookstore.book.tag.service.TagService;
import com.nhnacademy.bookstore.entity.tag.Tag;

/**
 * tag CRUD 서비스(read 제외)
 *
 * @author 정주혁
 */
@Service
@Transactional
public class TagServiceImpl implements TagService {
	@Autowired
	private TagRepository tagRepository;

	@Override
	public List<TagResponse> getAllTags() {
		List<Tag> tags = tagRepository.findAll();
		List<TagResponse> tagResponses = new ArrayList<>();
		for (Tag tag : tags) {
			tagResponses.add(TagResponse.builder()
				.id(tag.getId())
				.name(tag.getName())
				.build());
		}
		return tagResponses;
	}

	/**
	 * tag 추가
	 *
	 * @param tag 추가할 태그 이름
	 *
	 */
	@Override
	public Long createTag(CreateTagRequest tag) {
		Tag tagEntity = new Tag();
		if (tagRepository.findByName(tag.name()).isPresent()) {
			throw new AlreadyHaveTagException("태그가 이미 있습니다.");
		}
		tagEntity.setName(tag.name());

		return tagRepository.save(tagEntity).getId();
	}

	/**
	 * tag 제거
	 *
	 * @param tag 제거할 태그 id
	 *
	 */
	@Override
	public void deleteTag(DeleteTagRequest tag) {
		if (!tagRepository.existsById(tag.tagId())) {
			throw new NotExistsTagException("해당 태그가 없습니다.");
		}
		tagRepository.deleteById(tag.tagId());
	}

	/**
	 * tag 수정
	 *
	 * @param tag 변경할 태그 내용
	 */
	@Override
	public Long updateTag(UpdateTagRequest tag) {
		Tag tagEntity = tagRepository.findById(tag.tagId()).orElse(null);
		if (tagEntity == null) {
			throw new NotExistsTagException("해당 태그가 없습니다.");
		}
		tagEntity.setName(tag.tagName());
		tagRepository.save(tagEntity);
		return tagEntity.getId();
	}

	@Override
	public Page<TagResponse> getAllAdminTags(Pageable pageable) {
		return tagRepository.readAdminBookList(pageable);
	}
}
