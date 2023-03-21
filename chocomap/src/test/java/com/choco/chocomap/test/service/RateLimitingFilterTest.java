package com.choco.chocomap.test.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.choco.chocomap.searchmap.common.ratelimiting.RateLimitingFilter;

@SpringBootTest
@AutoConfigureMockMvc
public class RateLimitingFilterTest {

	@Autowired
    private MockMvc mockMvc;
	    
	@InjectMocks
	RateLimitingFilter rateLimitingFilter;
	
	@Test
    public void testDoFilterInternal() throws Exception {
		int num = 10;
		while(num>1) {
			mockMvc.perform(get("/search?keyword=카카오"))
            	.andExpect(status().isOk());
			Thread.sleep(1000);
		}
		
		mockMvc.perform(get("/search?keyword=카카오"))
        	.andExpect(status().isTooManyRequests());
    }
}
