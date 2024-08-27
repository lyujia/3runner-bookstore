package com.nhnacademy.bookstore.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ValidationUtils {

    public static void validateBindingResult(BindingResult bindingResult, RuntimeException exception) {
        if (bindingResult.hasErrors()) {
            throw exception;
        }
    }
}