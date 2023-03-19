package com.choco.chocomap.searchmap.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class ErrorResponse  {
	
	private int rspCode;
    private String rspMessage;
	
    public ErrorResponse (int rspCode, String rspMessage){
		this.rspCode = rspCode;
		this.rspMessage = rspMessage;
	}
}