package com.choco.chocomap.searchmap.common.exceptions;

import org.springframework.http.HttpStatus;

import com.choco.chocomap.searchmap.common.ErrorResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponseException extends RuntimeException {
	
	private final HttpStatus httpStatus;
    private final ErrorResponse errorResponse;

    public ErrorResponseException (HttpStatus httpStatus, ErrorResponse errorResponse){
    	this.httpStatus = httpStatus;
		this.errorResponse = errorResponse;
	}
    
}
