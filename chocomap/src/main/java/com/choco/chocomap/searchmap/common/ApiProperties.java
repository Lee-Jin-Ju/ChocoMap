package com.choco.chocomap.searchmap.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Configuration
@Getter
@Setter
@Builder
@NoArgsConstructor
@ConfigurationProperties(prefix = "api")
public class ApiProperties {
	private Kakao kakao;
    private Naver naver;
    
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class Kakao {
        private String url;
        private String key;
        
        Kakao (String url, String key){
    		this.url = url;
    		this.key = key;
    	}
        
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class Naver {
        private String url;
        private String clientId;
        private String clientSecret;
        
        Naver (String url, String clientId, String clientSecret){
    		this.url = url;
    		this.clientId = clientId;
    		this.clientSecret = clientSecret;
    	}
    }
    
    ApiProperties (Kakao kakao, Naver naver){
		this.kakao = kakao;
		this.naver = naver;
	}
}
