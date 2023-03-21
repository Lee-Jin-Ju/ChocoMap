package com.choco.chocomap.searchmap.common.swagger;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	private static final String API_NAME = "Search Place Open API";
    private static final String API_VERSION = "1.0";
    private static final String API_DESCRIPTION = "장소 검색 OPEN API 명세서 (카카오 장소 검색 API, 네이버 장소 검색 API)";

    @Bean
    public Docket api() {
        Parameter parameterBuilder = new ParameterBuilder()
            .name(HttpHeaders.AUTHORIZATION)
            .description("Access Tocken")
            .modelRef(new ModelRef("string"))
            .parameterType("header")
            .required(false)
            .build();

        List<Parameter> globalParamters = new ArrayList<>();
        globalParamters.add(parameterBuilder);

        return new Docket(DocumentationType.SWAGGER_2)
                .globalOperationParameters(globalParamters)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.choco.chocomap.searchmap"))
                .paths(PathSelectors.any())
                .build();
    }

    public ApiInfo apiInfo() {
	return new ApiInfoBuilder()
		.title(API_NAME)
		.version(API_VERSION)
		.description(API_DESCRIPTION)
		.build();
    }
}
