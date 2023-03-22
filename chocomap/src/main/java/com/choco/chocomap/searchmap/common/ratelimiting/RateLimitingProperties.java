package com.choco.chocomap.searchmap.common.ratelimiting;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@Setter
@Builder
@NoArgsConstructor
@ConfigurationProperties(prefix = "rate")
public class RateLimitingProperties {
	public int capacity;
	public int refillRate;
	public int refillDuration;
    
    RateLimitingProperties (int capacity, int refillRate, int refillDuration){
		this.capacity = capacity;
		this.refillRate = refillRate;
		this.refillDuration = refillDuration;
	}
}
