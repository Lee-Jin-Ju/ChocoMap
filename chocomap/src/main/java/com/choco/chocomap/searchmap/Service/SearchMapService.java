package com.choco.chocomap.searchmap.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.choco.chocomap.searchmap.Dao.SearchMapDao;
import com.choco.chocomap.searchmap.Dto.KeywordDto;
import com.choco.chocomap.searchmap.Dto.PlaceDto;
import com.choco.chocomap.searchmap.Dto.SearchListPlaceDto;
import com.choco.chocomap.searchmap.common.ErrorResponse;
import com.choco.chocomap.searchmap.common.ErrorResponseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class SearchMapService {


    @Value("${kakao.api.key}")
    private String apiKey;
    
    @Value("${X-Naver-Client-Id}")
    private String clientId;
    
    @Value("${X-Naver-Client-Secret}")
    private String clientSecret;
    
    @Value("${kakaoApiUrl}")
    private String kakaoApiUrl;
    
    @Value("${naverApiUrl}")
    private String naverApiUrl;
    
    List<PlaceDto> listPlacesResKakao;
    List<KeywordDto> listKeywordDto;
    
    @Autowired
    SearchMapDao searchMapDao;
    
    public SearchListPlaceDto searchKeywordMain(String keyword) {
    	SearchListPlaceDto searchListPlaceDto = new SearchListPlaceDto();
    	
    	Map<String, PlaceDto> resultMapKakao = new HashMap<>();
    	Map<String, PlaceDto> resultMapNaver = new HashMap<>();
    	Map<String, PlaceDto> resultMapResultList = new HashMap<>();
    	Map<String, PlaceDto> resultMapNaverPlus = new HashMap<>();
    	List<PlaceDto> returnSearchResult = new ArrayList<>();
    	
    	try {
            String searchResultForKakao = searchKeywordForKakao(keyword,1,5);
            
            List<PlaceDto> listPlacesResKakao = searchKeywordJsonToDto(searchResultForKakao, "documents", "place_name", "road_address_name", "address_name", "place_url", "category_name", "phone", "Kakao");
    		
    		String searchResultForNaver = searchKeywordForNaver(keyword,1,5);
    		List<PlaceDto> listPlacesResNaver = searchKeywordJsonToDto(searchResultForNaver, "items", "title", "roadAddress", "address", "link", "category", "telephone", "Naver");
    		
    		
    		resultMapKakao = searchKeywordListToMap(resultMapKakao, listPlacesResKakao);
    		resultMapNaver = searchKeywordListToMap(resultMapNaver, listPlacesResNaver);
    		
    		int removeCount = removeResultKeywordList(returnSearchResult, resultMapKakao, resultMapNaver, "init");
    		
    		if(removeCount>0) {
    			searchResultForNaver = searchKeywordForNaver(keyword,2,removeCount);
    			listPlacesResNaver = searchKeywordJsonToDto(searchResultForNaver, "items", "title", "roadAddress", "address", "link", "category", "telephone", "Naver");
    			resultMapResultList = searchKeywordListToMap(resultMapResultList, returnSearchResult);
    			resultMapNaverPlus = searchKeywordListToMap(resultMapNaverPlus, listPlacesResNaver);
    			
    			removeResultKeywordList(returnSearchResult, resultMapResultList, resultMapNaverPlus, "listPlus");
    		}
    		
            searchListPlaceDto = SearchListPlaceDto.builder()
            			.listPlace((List<PlaceDto>)returnSearchResult)
            			.place_tot_count(returnSearchResult.size())
            			.build();
           
//          동시성 문제를 해결을 위한 락 설정
            searchMapDao.lockKeyword(keyword);
            searchMapDao.insertKeyword(keyword);
        
    	} catch (HttpClientErrorException ex) {
    		
    	    throw new ErrorResponseException(HttpStatus.BAD_REQUEST, new ErrorResponse(ex.getRawStatusCode(), ex.getStatusText()));
    	    
    	} catch (HttpServerErrorException ex) {
    		
    	    throw new ErrorResponseException(HttpStatus.BAD_REQUEST, new ErrorResponse(ex.getRawStatusCode(), ex.getStatusText()));
    	    
    	} catch (Exception ex) {
    		
    	    throw new ErrorResponseException(HttpStatus.BAD_REQUEST, new ErrorResponse(500, ex.getMessage()));
    	}

        return searchListPlaceDto;
    	
    }
    
    public int removeResultKeywordList(List<PlaceDto> returnSearchResult, Map<String, PlaceDto> resultMapKakao, Map<String, PlaceDto> resultMapNaver, String flag) {
    	
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
        	if(flag.equals("init")) {
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
    
//    키워드 유사도 비교(카카오와 네이버 검색결과 비교)
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
    
//    각각의 검색결과간 중복제거(장소명과 주소만)
    public Map<String, PlaceDto> searchKeywordListToMap(Map<String, PlaceDto> resultMap, List<PlaceDto> listPlacesResName) {
    	
    	for(PlaceDto resDto : listPlacesResName) {
			String keyName = resDto.getPlace_name();
	        String sumKeyName = keyName + resDto.getPlace_address_name();
	        String resultKeyName = searchCommonCode(sumKeyName, "keyMapName");
	        resultMap.put(resultKeyName, resDto);
		}
    	
        return resultMap;
        
    }
    
    public String searchCommonCode(String resultCommonCode, String flag) {
			
		Function<String, String> removeSpaces = s -> s.replaceAll("\\s+", "");
        Function<String, String> removeTags = s -> s.replaceAll("<[^>]*>", "");
        Function<String, String> removeHtmlSpecialChars = s -> s.replaceAll("&apos;", "")
        														.replaceAll("&amp;", "")
												    	        .replaceAll("&nbsp;", "")
												    	        .replaceAll("&lt;", "")
												    	        .replaceAll("&gt;", "");
        switch(flag) {
        	case "keyMapName" :
        		resultCommonCode = removeSpaces
										.andThen(removeHtmlSpecialChars)
										.andThen(removeTags)
										.apply(resultCommonCode);
        	case "keyName" : 
        		resultCommonCode = removeTags
										.andThen(removeHtmlSpecialChars)
										.apply(resultCommonCode);
        }
        
        return resultCommonCode;
        
    }
    
    public List<PlaceDto> searchKeywordJsonToDto(String json, String listName, String placeName, String placeRoadAdd, String placeAdd, String placeUrl, String ctg, String phn, String searchSite) {
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	List<PlaceDto> listPlaces = new ArrayList<>();
    	
    	try {
			Map<String, Object> responseJson = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            List<Map<String, Object>> documents = objectMapper.convertValue(responseJson.get(listName), new TypeReference<List<Map<String, Object>>>() {});

            for (Map<String, Object> document : documents) {
            	PlaceDto dto = PlaceDto.builder()
                		.place_name(searchCommonCode((String) document.get(placeName), "keyName"))
                		.place_road_address_name((String) document.get(placeRoadAdd))
                		.place_address_name((String) document.get(placeAdd))
                		.place_url((String) document.get(placeUrl))
                		.place_category_name((String) document.get(ctg))
                		.place_phone((String) document.get(phn))
                		.place_search_site((String) searchSite)
                		.build();
            	listPlaces.add(dto);
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }

        return listPlaces;
    	
    }
    
//    새로운 검색 API 제공자의 추가 시 공통부분
    public String searchKeywordApiResult(String url, HttpHeaders headers) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        
        return response.getBody();
        
    }

    public String searchKeywordForKakao(String keyword, int page, int size) {
        String url = kakaoApiUrl + "?query=" + keyword + "&size="+size;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);
        
        return searchKeywordApiResult(url, headers);
        
    }
        
    public String searchKeywordForNaver(String keyword, int page, int size) {
        String url = naverApiUrl + "?query=" + keyword + "&display="+size+"&start="+page;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.add("X-Naver-Client-Secret", clientSecret);
        
        return searchKeywordApiResult(url, headers);
         
    }
    
    public List<KeywordDto> selectKeywordList() {
        
    	listKeywordDto = searchMapDao.selectKeywordList();
        
        return listKeywordDto;
    }
    
	
}
