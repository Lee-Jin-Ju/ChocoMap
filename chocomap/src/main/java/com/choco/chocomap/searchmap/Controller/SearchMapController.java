package com.choco.chocomap.searchmap.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choco.chocomap.searchmap.Dto.KeywordDto;
import com.choco.chocomap.searchmap.Dto.SearchListPlaceDto;
import com.choco.chocomap.searchmap.Service.SearchMapService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
public class SearchMapController {
	
	private final SearchMapService searchMapService;

    public SearchMapController(SearchMapService searchMapService) {
        this.searchMapService = searchMapService;
    }

    @GetMapping("/search")
    @ApiOperation(value = "장소 검색조회", notes = "장소(검색) 키워드를 입력하면 카카오와 네이버 장소검색 API 목록을 조회 (카카오 우선)")
    @ApiImplicitParam(name = "keyword", value = "장소(검색) 키워드")
    public SearchListPlaceDto searchKeyword(@RequestParam("keyword") String keyword) {
        return searchMapService.searchKeywordMain(keyword);
    }
    
    @GetMapping("/topList")
    @ApiOperation(value = "상위 검색어 조회", notes = "검색 키워드 상위 10개 목록 조회 (조회수 내림차순, 이름 오름차순)")
    public List<KeywordDto> selectKeywordList() {
    	return searchMapService.selectKeywordList();
    }
    

}

