package com.nhnacademy.bookstore.purchase.payment.controller;
import com.nhnacademy.bookstore.purchase.payment.dto.CreatePaymentGuestRequest;
import com.nhnacademy.bookstore.purchase.payment.exception.TossPaymentException;
import com.nhnacademy.bookstore.purchase.payment.service.PaymentGuestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * 페이먼츠 비회원 컨트롤러.
 *
 * @author 김병우
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentGuestController {
    private final PaymentGuestService paymentGuestService;

    @RequestMapping(value = "/bookstore/payments/guests/confirm")
    public ResponseEntity<JSONObject> confirmPayment(
            @RequestHeader(value = "Member-Id", required = false) Long memberId,
            @RequestParam(required = false)  Long cartId,
            @RequestParam(required = false)  String address,
            @RequestParam(required = false)  String password,
            @RequestParam(required = false) String isPacking,
            @RequestParam(required = false) String shipping,
            @RequestBody String jsonBody) throws Exception {

        log.info("토스 페이 요청 컨트롤러 진입");

        JSONParser parser = new JSONParser();
        String orderId;
        String amount;
        String paymentKey;
        try {
            // 클라이언트에서 받은 JSON 요청 바디입니다.
            JSONObject requestData = (JSONObject) parser.parse(jsonBody);
            paymentKey = (String) requestData.get("paymentKey");
            orderId = (String) requestData.get("orderId");
            amount = (String) requestData.get("amount");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        ;
        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);


        log.info("토스 페이 요청 컨트롤러 진입");
        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);


        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;
        log.info("Response code from Toss Payments: {}", code);

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        log.info("Toss getInputStream: {}", connection.getInputStream());
        log.info("Toss getErrorStream: {}", connection.getErrorStream());
        // 결제 성공 및 실패 비즈니스 로직을 구현하세요.
        LocalDate localDate = LocalDate.parse(shipping, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (isSuccess) {
            log.info("토스 페이 서비스");
            paymentGuestService.payment(CreatePaymentGuestRequest.builder()
                    .shippingDate(localDate.atStartOfDay(ZoneId.systemDefault()))
                    .password(password)
                    .cartId(cartId)
                    .amount(Integer.valueOf(amount))
                    .isPacking(Boolean.valueOf(isPacking))
                    .orderId(orderId)
                    .road(address)
                            .paymentKey(paymentKey)
                    .build()
            );

        } else {
            log.info("토스 페이 예외처리");
            throw new TossPaymentException("토스 최종 결제가 실패하였습니다.");
        }

        log.info("토스 페이 끝");
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();

        return ResponseEntity.status(code).body(jsonObject);

    }
}
