package com.choco.chocomap.searchmap.common.ratelimiting;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@Configuration
public class AppRateLimitingConfig implements WebMvcConfigurer {

	
	@Bean
	public RateLimitingFilter rateLimitingFilter(RateLimitingProperties rateLimitingProperties) {
	   return new RateLimitingFilter(rateLimitingProperties);
    }
	
    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilterRegistrationBean(RateLimitingFilter rateLimitingFilter) {
        FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(rateLimitingFilter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}

