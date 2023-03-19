package com.choco.chocomap.searchmap.common;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class CommonApiError  {
	
	private HttpStatus status;
    private String rspMessage;
    private LocalDateTime timestamp;
	
    CommonApiError (HttpStatus status, String rspMessage, LocalDateTime timestamp){
		this.status = status;
		this.rspMessage = rspMessage;
		this.timestamp = timestamp;
	}
}
