package com.choco.chocomap.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.choco.chocomap.searchmap.Dao.SearchMapDao;
import com.choco.chocomap.searchmap.Dto.KeywordDto;
import com.choco.chocomap.searchmap.Dto.SearchListPlaceDto;
import com.choco.chocomap.searchmap.Service.SearchMapService;
import com.choco.chocomap.searchmap.common.ApiProperties;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SearchMapServiceTest {

    @Mock
    private SearchMapDao searchMapDao;

    @Mock
    private ApiProperties apiProperties;

    @InjectMocks
    private SearchMapService searchMapService;

    @BeforeEach
    public void setUp() {
        ApiProperties.Kakao kakao = new ApiProperties.Kakao();
        kakao.setKey("180a6eb4214628e3ac6c254cf51c9fea");
        kakao.setUrl("https://dapi.kakao.com/v2/local/search/keyword.json");
        Mockito.lenient().when(apiProperties.getKakao()).thenReturn(kakao);

        ApiProperties.Naver naver = new ApiProperties.Naver();
        naver.setClientId("LIHpWJjG8Xr7WXJJ5Vba");
        naver.setClientSecret("RbxNk4s3dI");
        naver.setUrl("https://openapi.naver.com/v1/search/local.json");
        Mockito.lenient().when(apiProperties.getNaver()).thenReturn(naver);
    }

    @Test
    public void testSearchKeywordMain() {
        String keyword = "떡볶이";
        SearchListPlaceDto searchListPlaceDto = searchMapService.searchKeywordMain(keyword);

        assertNotNull(searchListPlaceDto);
        assertNotNull(searchListPlaceDto.getListPlace());
        assertEquals(searchListPlaceDto.getPlace_tot_count(), searchListPlaceDto.getListPlace().size());

        verify(searchMapDao, times(1)).lockKeyword(keyword);
        verify(searchMapDao, times(1)).insertKeyword(keyword);
    }

    @Test
    public void testSelectKeywordList() {
        List<KeywordDto> keywordDtoList = searchMapService.selectKeywordList();

        assertNotNull(keywordDtoList);

        verify(searchMapDao, times(1)).selectKeywordList();
    }
}
