package com.nhnacademy.bookstore.global.exceptionhandler;

import java.nio.channels.AlreadyBoundException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.exception.CreateBookRequestFormException;
import com.nhnacademy.bookstore.book.book.exception.UpdateBookRequestFormException;
import com.nhnacademy.bookstore.book.category.exception.CreateCategoryRequestException;
import com.nhnacademy.bookstore.book.category.exception.UpdateCategoryRequestException;
import com.nhnacademy.bookstore.book.image.exception.NotFindImageException;
import com.nhnacademy.bookstore.book.tag.exception.AlreadyHaveTagException;
import com.nhnacademy.bookstore.member.address.exception.AddressFullException;
import com.nhnacademy.bookstore.member.address.exception.AddressNotExistsException;
import com.nhnacademy.bookstore.member.member.exception.AlreadyExistsEmailException;
import com.nhnacademy.bookstore.member.member.exception.LoginFailException;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.purchase.bookcart.exception.BookCartArgumentErrorException;
import com.nhnacademy.bookstore.purchase.payment.exception.TossPaymentException;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseAlreadyExistException;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseDoesNotExistException;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseFormArgumentErrorException;
import com.nhnacademy.bookstore.purchase.purchase.exception.PurchaseNoAuthorizationException;
import com.nhnacademy.bookstore.purchase.purchasebook.exception.ImPossibleAccessPurchaseBookException;
import com.nhnacademy.bookstore.purchase.refund.exception.CreateRefundRequestFormException;
import com.nhnacademy.bookstore.purchase.refund.exception.ImpossibleAccessRefundException;
import com.nhnacademy.bookstore.purchase.refund.exception.NotExistsRefund;
import com.nhnacademy.bookstore.purchase.refund.exception.NotExistsRefundRecord;
import com.nhnacademy.bookstore.util.ApiResponse;

/**
 * Error Handler Controller
 * @author 김병우
 */
@RestControllerAdvice
public class WebControllerAdvice {

	// 응답, 성공,실패 폼 맞추기

	/**
	 * INTERNAL_SERVER_ERROR 처리 메소드
	 * @param ex
	 * @param model
	 * @return ApiResponse<ErrorResponseForm>
	 */
	@ExceptionHandler(
		RuntimeException.class
	)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiResponse<ErrorResponseForm> runtimeExceptionHandler(Exception ex, Model model) {

		return ApiResponse.fail(500,
			new ApiResponse.Body<>(
				ErrorResponseForm.builder()
					.title(ex.getMessage())
					.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
					.timestamp(ZonedDateTime.now().toString())
					.build())
		);
	}

	/**
	 * BAD_REQUEST 처리 메소드
	 * @param ex
	 * @param model
	 * @return ApiResponse<ErrorResponseForm>
	 */
	@ExceptionHandler({
		CreateBookRequestFormException.class,
		MemberNotExistsException.class,
		AlreadyExistsEmailException.class,
		AddressNotExistsException.class,
		LoginFailException.class,
		AddressFullException.class,
		AlreadyBoundException.class,
		PurchaseFormArgumentErrorException.class,
		PurchaseAlreadyExistException.class,
		BookCartArgumentErrorException.class,
		TossPaymentException.class,
		UpdateBookRequestFormException.class,
		AlreadyHaveTagException.class,
		CreateRefundRequestFormException.class,
		CreateCategoryRequestException.class,
		UpdateCategoryRequestException.class

	})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<ErrorResponseForm> badRequestHandler(Exception ex, Model model) {
		return ApiResponse.badRequestFail(
			new ApiResponse.Body<>(ErrorResponseForm.builder()
				.title(ex.getMessage())
				.status(HttpStatus.BAD_REQUEST.value())
				.timestamp(ZonedDateTime.now().toString())
				.build())
		);

	}

	/**
	 * NOT_FOUND 처리 메소드
	 * @param ex
	 * @param model
	 * @return ApiResponse<ErrorResponseForm>
	 *
	 * @author 정주혁
	 */
	@ExceptionHandler({
		PurchaseDoesNotExistException.class,
		PurchaseDoesNotExistException.class,
		NotFindImageException.class,
		BookDoesNotExistException.class,
		NotExistsRefund.class,
		NotExistsRefundRecord.class
	})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiResponse<ErrorResponseForm> notFoundHandler(Exception ex, Model model) {
		return ApiResponse.notFoundFail(
			new ApiResponse.Body<>(ErrorResponseForm.builder()
				.title(ex.getMessage())
				.status(HttpStatus.NOT_FOUND.value())
				.timestamp(ZonedDateTime.now().toString())
				.build())
		);
	}

	/**
	 * METHOD_NOT_ALLOWED 처리 메소드
	 * @param ex
	 * @param model
	 * @return ApiResponse<ErrorResponseForm>
	 *
	 * @author 정주혁
	 */

	@ExceptionHandler({
		PurchaseNoAuthorizationException.class,

	})
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ApiResponse<ErrorResponseForm> unauthorizedHandler(Exception ex, Model model) {
		return new ApiResponse<>(
			new ApiResponse.Header(false, HttpStatus.METHOD_NOT_ALLOWED.value()),
			new ApiResponse.Body<>(ErrorResponseForm.builder()
				.title(ex.getMessage())
				.status(HttpStatus.UNAUTHORIZED.value())
				.timestamp(ZonedDateTime.now().toString())
				.build())
		);
	}


	@ExceptionHandler({
		ImpossibleAccessRefundException.class,
		ImPossibleAccessPurchaseBookException.class
	})
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ApiResponse<ErrorResponseForm> forbiddenHandler(Exception ex, Model model) {
		return ApiResponse.forbiddenFail(new ApiResponse.Body<>(ErrorResponseForm.builder()
			.title(ex.getMessage())
			.status(HttpStatus.FORBIDDEN.value())
			.timestamp(ZonedDateTime.now().toString())
			.build()));


	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiResponse<ErrorResponseForm> handleValidationExceptions(MethodArgumentNotValidException ex, Model model) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError)error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return new ApiResponse<>(
			new ApiResponse.Header(false, 400),
			new ApiResponse.Body<>(ErrorResponseForm.builder()
				.title(errors.toString())
				.status(HttpStatus.BAD_REQUEST.value())
				.timestamp(ZonedDateTime.now().toString()).build())
		);
	}

	//    @ExceptionHandler({})
	//    @ResponseStatus(HttpStatus.NOT_FOUND)
	//    public ApiResponse<ErrorResponseForm> notFoundHandler(Exception ex, Model model) {
	//        return new ApiResponse<>(
	//                new ApiResponse.Header(false, 404, "not found"),
	//                new ApiResponse.Body<>(ErrorResponseForm.builder()
	//                        .title(ex.getMessage())
	//                        .status(HttpStatus.NOT_FOUND.value())
	//                        .timestamp(ZonedDateTime.now().toString())
	//                        .build())
	//        );
	//    }
	//
	//
	//    @ExceptionHandler({})
	//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
	//    public ApiResponse<ErrorResponseForm> unauthorizedHandler(Exception ex, Model model) {
	//        return new ApiResponse<>(
	//                new ApiResponse.Header(false, 405, "unauthorized"),
	//                new ApiResponse.Body<>(ErrorResponseForm.builder()
	//                        .title(ex.getMessage())
	//                        .status(HttpStatus.UNAUTHORIZED.value())
	//                        .timestamp(ZonedDateTime.now().toString())
	//                        .build())
	//        );
	//    }
}
