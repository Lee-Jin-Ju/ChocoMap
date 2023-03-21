package com.choco.chocomap.searchmap.common.ratelimiting;

import java.util.concurrent.TimeUnit;

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
@ConfigurationProperties(prefix = "rate-limiting")
public class RateLimitingProperties {
	private int capacity;
    private int refillTokens;
    private int refillDuration;
    private TimeUnit refillUnit;

    RateLimitingProperties (int capacity, int refillTokens, int refillDuration, TimeUnit refillUnit){
		this.capacity = capacity;
		this.refillTokens = refillTokens;
		this.refillDuration = refillDuration;
		this.refillUnit = refillUnit;
	}
}
