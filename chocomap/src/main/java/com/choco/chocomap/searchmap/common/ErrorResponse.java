package com.choco.chocomap.searchmap.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class ErrorResponse  {
	
	private String rsp_code;
    private String rsp_message;

    public ErrorResponse (String rsp_code, String rsp_message){
		this.rsp_code = rsp_code;
		this.rsp_message = rsp_message;
	}
}