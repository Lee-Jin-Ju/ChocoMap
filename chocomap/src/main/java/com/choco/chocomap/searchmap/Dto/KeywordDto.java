package com.choco.chocomap.searchmap.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class KeywordDto {
	
	private String keyword;
	private int view_cnt;
	
	KeywordDto (String keyword, int view_cnt){
		this.keyword = keyword;
		this.view_cnt = view_cnt;
	}
}
