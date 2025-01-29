package com.example.ecommerce.exception;

import org.springframework.http.HttpStatusCode;

public class ApiException extends RuntimeException {
    HttpStatusCode statusCode;
    String message;

    public ApiException(HttpStatusCode statusCode, String message) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
