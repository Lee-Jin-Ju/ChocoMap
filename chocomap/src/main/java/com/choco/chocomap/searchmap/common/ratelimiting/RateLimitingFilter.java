package com.choco.chocomap.searchmap.common.ratelimiting;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.choco.chocomap.searchmap.common.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import io.swagger.annotations.ApiOperation;

public class RateLimitingFilter extends OncePerRequestFilter {
	

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Autowired
    RateLimitingProperties rateLimitingProperties;

    @Override
    @ApiOperation(value = "트래픽 제한 설정(API 호출횟수를 제한)", notes = "request에 있는 ip주소를 가져와 Bucket에 담아주고 요청처리 용량은 10, 20초마다 10개의 요청량 재생성 설정")
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    	String ipAddress = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ipAddress, k ->
                Bucket4j.builder()
                        .addLimit(Bandwidth.classic(rateLimitingProperties.getCapacity(), Refill.intervally(rateLimitingProperties.getRefillRate(), Duration.ofSeconds(rateLimitingProperties.getRefillDuration()))))
                        .build()
        );
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            filterChain.doFilter(request, response);
        } else {
        	ErrorResponse errorResponse = new ErrorResponse(String.valueOf(HttpStatus.TOO_MANY_REQUESTS.value()), "Too many requests");
            String json = new ObjectMapper().writeValueAsString(errorResponse);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write(json);
            response.getWriter().flush();
        }
    }

    @Override
    @ApiOperation(value = "버킷 초기화", notes = "요청 재생성시 버킷 초기화")
    public void destroy() {
        super.destroy();
        buckets.clear();
    }
}

