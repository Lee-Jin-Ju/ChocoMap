package com.choco.chocomap.searchmap.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.choco.chocomap.searchmap.Dao.SearchMapDao;
import com.choco.chocomap.searchmap.Dto.KeywordDto;
import com.choco.chocomap.searchmap.Dto.PlaceDto;
import com.choco.chocomap.searchmap.Dto.SearchListPlaceDto;
import com.choco.chocomap.searchmap.common.ApiFieldForm;
import com.choco.chocomap.searchmap.common.ApiFlagForm;
import com.choco.chocomap.searchmap.common.ApiProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;

@Service
@Transactional
public class SearchMapService {

    
    @Autowired
    SearchMapDao searchMapDao;
    
    @Autowired
    ApiProperties apiProperties;

    	
    public SearchListPlaceDto searchKeywordMain(String keyword) {
    	SearchListPlaceDto searchListPlaceDto = new SearchListPlaceDto();
    	
    	Map<String, PlaceDto> resultMapKakao = new HashMap<>();
    	Map<String, PlaceDto> resultMapNaver = new HashMap<>();
    	Map<String, PlaceDto> resultMapResultList = new HashMap<>();
    	Map<String, PlaceDto> resultMapNaverPlus = new HashMap<>();
    	List<PlaceDto> returnSearchResult = new ArrayList<>();
    	
		List<PlaceDto> listPlacesResKakao = searchKeywordJsonToDto(searchKeywordForKakao(keyword,1,5), ApiFieldForm.KAKAO);
		List<PlaceDto> listPlacesResNaver = searchKeywordJsonToDto(searchKeywordForNaver(keyword,1,5), ApiFieldForm.NAVER);
		
		
		resultMapKakao = searchKeywordListToMap(resultMapKakao, listPlacesResKakao);
		resultMapNaver = searchKeywordListToMap(resultMapNaver, listPlacesResNaver);
		
		int removeCount = removeResultKeywordList(returnSearchResult, resultMapKakao, resultMapNaver, ApiFlagForm.INIT);
		
		if(removeCount>0) {
			listPlacesResNaver = searchKeywordJsonToDto(searchKeywordForNaver(keyword,2,removeCount), ApiFieldForm.NAVER);
			resultMapResultList = searchKeywordListToMap(resultMapResultList, returnSearchResult);
			resultMapNaverPlus = searchKeywordListToMap(resultMapNaverPlus, listPlacesResNaver);
			
			removeResultKeywordList(returnSearchResult, resultMapResultList, resultMapNaverPlus, ApiFlagForm.LIST_PLUS);
		}

		searchListPlaceDto = SearchListPlaceDto.builder()
        			.listPlace((List<PlaceDto>)returnSearchResult)
        			.place_tot_count(returnSearchResult.size())
        			.build();
       
//          동시성 문제를 해결을 위한 락 설정
        searchMapDao.lockKeyword(keyword);
        searchMapDao.insertKeyword(keyword);
        
        return searchListPlaceDto;
    	
    }
    
    @ApiOperation(value = "키워드 중복제거", notes = "카카오와 네이버 검색 키워드 이름과 주소 유사도 80%이상 시 네이버 키워드 제거")
    public int removeResultKeywordList(List<PlaceDto> returnSearchResult, Map<String, PlaceDto> resultMapKakao, Map<String, PlaceDto> resultMapNaver, ApiFlagForm flag) {
    	
    	int removeCount = 0;
        for( String keyKakao : resultMapKakao.keySet() ){
        	
        	Iterator<String> keyNaverIterator = resultMapNaver.keySet().iterator();

        	while (keyNaverIterator.hasNext()) {
                String keyNaver = keyNaverIterator.next();
        		long dpNum = searchKeywordLCSCount(keyKakao, keyNaver);
        		long maxNum;
        		
        		if(keyKakao.length() >= keyNaver.length()) {
        			maxNum = keyKakao.length();
        		}else {
        			maxNum = keyNaver.length();
        		}
        		
        		if((dpNum/(double) maxNum) >= 0.8) {
        			keyNaverIterator.remove();
        			removeCount++;
        		}
        		
            }
        	switch(flag) {
        	case INIT :
        		returnSearchResult.add(resultMapKakao.get(keyKakao));
        	}
        }
        
        Iterator<String> mapNaverKeys = resultMapNaver.keySet().iterator();
        while( mapNaverKeys.hasNext() ){
    		String mapNaverKey = mapNaverKeys.next();
    		returnSearchResult.add(resultMapNaver.get(mapNaverKey));
    		
    	}
        
        return removeCount;
    	
    }
    
    @ApiOperation(value = "키워드 유사도 비교", notes = "키워드 유사도 비교(카카오와 네이버 검색결과 비교)")
    public int searchKeywordLCSCount(String keyKakao, String keyNaver) {
    	
    	int[][] dp = new int[keyKakao.length() + 1][keyNaver.length() + 1];

        for (int i = 1; i <= keyKakao.length(); i++) {
            for (int j = 1; j <= keyNaver.length(); j++) {
                if (keyKakao.charAt(i - 1) == keyNaver.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        
        return dp[keyKakao.length()][keyNaver.length()];
        
    }
    
    @ApiOperation(value = "같은 검색결과 내 중복제거", notes = "각각의 검색결과간 중복제거(장소명과 주소만)")
    public Map<String, PlaceDto> searchKeywordListToMap(Map<String, PlaceDto> resultMap, List<PlaceDto> listPlacesResName) {
    	
    	for(PlaceDto resDto : listPlacesResName) {
			String keyName = resDto.getPlace_name();
	        String sumKeyName = keyName + resDto.getPlace_address_name();
	        String resultKeyName = searchCommonCode(sumKeyName, ApiFlagForm.REMAP_NAME);
	        resultMap.put(resultKeyName, resDto);
		}
    	
        return resultMap;
        
    }
    
    @ApiOperation(value = "키워드 공백제거", notes = "키워드 공백과 태그, HTML 엔티티 제거")
    public String searchCommonCode(String resultCommonCode, ApiFlagForm flag) {
			
		Function<String, String> removeSpaces = s -> s.replaceAll("\\s+", "");
        Function<String, String> removeTags = s -> s.replaceAll("<[^>]*>", "");
        Function<String, String> removeHtmlSpecialChars = s -> s.replaceAll("&apos;", "")
        														.replaceAll("&amp;", "")
												    	        .replaceAll("&nbsp;", "")
												    	        .replaceAll("&lt;", "")
												    	        .replaceAll("&gt;", "");
        switch(flag) {
        	case REMAP_NAME :
        		resultCommonCode = removeSpaces
										.andThen(removeHtmlSpecialChars)
										.andThen(removeTags)
										.apply(resultCommonCode);
        	case RENAME : 
        		resultCommonCode = removeTags
										.andThen(removeHtmlSpecialChars)
										.apply(resultCommonCode);
        }
        
        return resultCommonCode;
        
    }
    
    @ApiOperation(value = "DTO 변환", notes = "API에서 응답받은 JSON을 DTO로 변환")
    public List<PlaceDto> searchKeywordJsonToDto(String json, ApiFieldForm apiField) {
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	List<PlaceDto> listPlaces = new ArrayList<>();
    	try {
	    	Map<String, Object> responseJson = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
	        List<Map<String, Object>> documents = objectMapper.convertValue(responseJson.get(apiField.getListKey()), new TypeReference<List<Map<String, Object>>>() {});
	        for (Map<String, Object> document : documents) {
	        	PlaceDto dto = PlaceDto.builder()
					                		.place_name(searchCommonCode((String) document.get(apiField.getPlaceKey()), ApiFlagForm.RENAME))
					                		.place_road_address_name((String) document.get(apiField.getRoadAddressKey()))
					                		.place_address_name((String) document.get(apiField.getAddressKey()))
					                		.place_url((String) document.get(apiField.getUrlKey()))
					                		.place_category_name((String) document.get(apiField.getCategoryKey()))
					                		.place_phone((String) document.get(apiField.getPhoneKey()))
					                		.place_search_site((String) apiField.getSiteKey())
					                		.build();
	        	listPlaces.add(dto);
	        }
    	} catch (Exception e) {
            e.printStackTrace();
        }

        return listPlaces;
    	
    }
    
    @ApiOperation(value = "OPEN API 통신", notes = "새로운 검색 API 제공자의 추가 시 공통부분")
    public String searchKeywordApiResult(String url, HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        
        return response.getBody();
        
    }
    
    @ApiOperation(value = "헤더 생성", notes = "전송해야할 헤더 한개일 때")
    private HttpHeaders createHeaders(String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        return headers;
    }
    
    @ApiOperation(value = "헤더 생성", notes = "전송해야할 헤더 두개일 때")
    private HttpHeaders createHeaders(String clientId, String clientSecret) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.add("X-Naver-Client-Secret", clientSecret);
        return headers;
    }

    @ApiOperation(value = "API 설정", notes = "새로운 검색 API 제공자 추가부분")
    public String searchKeywordForKakao(String keyword, int page, int size) {
    	String url = apiProperties.getKakao().getUrl() + "?query=" + keyword + "&size=" + size;
        HttpHeaders headers = createHeaders("KakaoAK " + apiProperties.getKakao().getKey());

        return searchKeywordApiResult(url, headers);
        
    }
        
    public String searchKeywordForNaver(String keyword, int page, int size) {
    	String url = apiProperties.getNaver().getUrl() + "?query=" + keyword + "&display=" + size + "&start=" + page;
        HttpHeaders headers = createHeaders(apiProperties.getNaver().getClientId(), apiProperties.getNaver().getClientSecret());

        return searchKeywordApiResult(url, headers);
         
    }
    
    @ApiOperation(value = "상위 조회수 키워드 목록", notes = "상위 10개의 검색 키워드 목록")
    public List<KeywordDto> selectKeywordList() {
    	List<KeywordDto> listKeywordDto = searchMapDao.selectKeywordList();
    	
        return listKeywordDto;
    }
    
	
}
