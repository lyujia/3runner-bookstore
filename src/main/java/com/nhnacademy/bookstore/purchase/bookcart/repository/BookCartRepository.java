package com.nhnacademy.bookstore.purchase.bookcart.repository;

import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.bookcart.BookCart;
import com.nhnacademy.bookstore.entity.cart.Cart;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * 도서장바구니 JPA 저장소.
 *
 * @author 김병우
 */
public interface BookCartRepository extends JpaRepository<BookCart, Long> {

	@Query("select bk from BookCart bk where bk.cart.id = :cartId")
	List<BookCart> findAllByCartId(Long cartId);

	List<BookCart> findAllByCart(Cart cart);

	void deleteByCart(Cart cart);

	Optional<BookCart> findBookCartByBookIdAndCartId(Long bookId, Long cartId);

    Optional<BookCart> findByBookAndCart(Book book, Cart cart);

	@Query("select bc from BookCart bc join Cart c on bc.cart.id = c.id where c.member.id = :memberId and bc.book.id = :bookId")
	Optional<BookCart> findBookCartByBookIdAndMemberId(Long bookId, Long memberId);

	boolean existsBookCartByBookAndCart(Book book, Cart cart);
}
