package com.nhnacademy.bookstore.refundRecord.reposiotry.impl;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.nhnacademy.bookstore.purchase.refundrecord.dto.response.ReadRefundRecordResponse;
import com.nhnacademy.bookstore.purchase.refundrecord.exception.NotExistsRefundRecordRedis;
import com.nhnacademy.bookstore.purchase.refundrecord.repository.impl.RefundRecordRedisRepositoryImpl;

@ExtendWith(MockitoExtension.class)
class RefundRecordRedisRepositoryImplTest {

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private HashOperations<String, Object, Object> hashOperations;

	@InjectMocks
	private RefundRecordRedisRepositoryImpl refundRecordRedisRepository;


	@Test
	void testCreate() {
		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		String hashName = "order123";
		Long id = 1L;
		ReadRefundRecordResponse response = ReadRefundRecordResponse.builder().id(id).quantity(2).price(1000).build();

		refundRecordRedisRepository.create(hashName, id, response);

		verify(hashOperations, times(1)).put(eq(hashName + ":"), eq(id.toString()), eq(response));
		verify(redisTemplate, times(1)).expire(eq(hashName + ":"), eq(1L), eq(TimeUnit.HOURS));
	}

	@Test
	void testUpdate() {
		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		String hashName = "order123";
		Long id = 1L;
		int quantity = 3;
		int price = 1000;
		ReadRefundRecordResponse existingResponse = ReadRefundRecordResponse.builder().id(id).quantity(2).price(1000).build();

		when(hashOperations.get(eq(hashName + ":"), eq(id.toString()))).thenReturn(existingResponse);

		Long result = refundRecordRedisRepository.update(hashName, id, quantity, price);

		assertThat(result).isEqualTo(id);
		verify(hashOperations, times(1)).put(eq(hashName + ":"), eq(id.toString()), any(ReadRefundRecordResponse.class));
		verify(redisTemplate, times(1)).expire(eq(hashName + ":"), eq(1L), eq(TimeUnit.HOURS));
	}

	@Test
	void testUpdate_NotExists() {
		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		String hashName = "order123";
		Long id = 1L;
		int quantity = 3;
		int price = 1000;

		when(hashOperations.get(eq(hashName + ":"), eq(id.toString()))).thenReturn(null);

		assertThrows(NotExistsRefundRecordRedis.class, () -> {
			refundRecordRedisRepository.update(hashName, id, quantity, price);
		});
	}

	@Test
	void testDelete() {
		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		String hashName = "order123";
		Long id = 1L;

		Long result = refundRecordRedisRepository.delete(hashName, id);

		assertThat(result).isEqualTo(id);
		verify(hashOperations, times(1)).delete(eq(hashName + ":"), eq(id.toString()));
	}

	@Test
	void testDeleteAll() {
		String hashName = "order123";

		refundRecordRedisRepository.deleteAll(hashName);

		verify(redisTemplate, times(1)).delete(eq(hashName + ":"));
	}

	@Test
	void testReadAll() {
		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		String hashName = "order123";
		ReadRefundRecordResponse response = ReadRefundRecordResponse.builder().id(1L).quantity(2).price(1000).build();

		when(hashOperations.values(eq(hashName + ":"))).thenReturn(Collections.singletonList(response));

		List<ReadRefundRecordResponse> responses = refundRecordRedisRepository.readAll(hashName);

		assertThat(responses).isNotEmpty();
		assertThat(responses.get(0).id()).isEqualTo(1L);
	}

	@Test
	void testIsHit() {
		String hashName = "order123";

		when(redisTemplate.hasKey(eq(hashName + ":"))).thenReturn(true);

		boolean result = refundRecordRedisRepository.isHit(hashName);

		assertThat(result).isTrue();
	}

	@Test
	void testDetailIsHit() {
		when(redisTemplate.opsForHash()).thenReturn(hashOperations);
		String hashName = "order123";
		Long id = 1L;

		when(hashOperations.hasKey(eq(hashName + ":"), eq(id.toString()))).thenReturn(true);

		boolean result = refundRecordRedisRepository.detailIsHit(hashName, id);

		assertThat(result).isTrue();
	}
}
