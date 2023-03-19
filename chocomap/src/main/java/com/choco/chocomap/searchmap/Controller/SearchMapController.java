package com.choco.chocomap.searchmap.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.choco.chocomap.searchmap.Dto.KeywordDto;
import com.choco.chocomap.searchmap.Dto.SearchListPlaceDto;
import com.choco.chocomap.searchmap.Service.SearchMapService;

@RestController
public class SearchMapController {
	
	private final SearchMapService searchMapService;

    public SearchMapController(SearchMapService searchMapService) {
        this.searchMapService = searchMapService;
    }

    @GetMapping("/search")
    public SearchListPlaceDto searchKeyword(@RequestParam("keyword") String keyword) {
        return searchMapService.searchKeywordMain(keyword);
    }
    
    @GetMapping("/kakao")
    public String searchKeywordForKakao(@RequestParam("keyword") String keyword) {
    	return searchMapService.searchKeywordForKakao(keyword,1,5);
    }

    @GetMapping("/naver")
    public String searchKeywordForNaver(@RequestParam("keyword") String keyword) {
        return searchMapService.searchKeywordForNaver(keyword,15,5);
    }
    
    @GetMapping("/topList")
    public List<KeywordDto> selectKeywordList() {
    	return searchMapService.selectKeywordList();
    }
    

}

