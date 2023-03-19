package com.choco.chocomap.searchmap.common.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.choco.chocomap.searchmap.common.CommonApiError;
import com.choco.chocomap.searchmap.common.ErrorResponse;
import com.choco.chocomap.searchmap.common.ErrorResponseException;

@RestControllerAdvice
public class ApiExceptionHandler {
	
	@ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<CommonApiError> handleKakaoApiException(ErrorResponseException ex) {
		ErrorResponse rspError = ex.getErrorResponse();
		
		CommonApiError apiError = CommonApiError.builder()
				.status(ex.getHttpStatus())
				.rspMessage(rspError.getRspMessage())
				.timestamp(LocalDateTime.now())
				.build();

        return new ResponseEntity<>(apiError, new HttpHeaders(), ex.getHttpStatus());
    }
}
