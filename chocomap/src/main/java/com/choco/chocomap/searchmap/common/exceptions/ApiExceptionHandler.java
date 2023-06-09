package com.choco.chocomap.searchmap.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.choco.chocomap.searchmap.common.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

	@ApiOperation(value = "HttpClientErrorException 예외처리", notes = "카카오, 네이버의 외부API 송수신 시 외부 API 에러응답값 전달-상태코드 4xx번대")
	@ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException ex) {
		log.debug("Unexpected httpclient exception occurred", ex);
        String responseBody = ex.getResponseBodyAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String code;
            String message;
            if(jsonNode.has("errorType")==true) {
            	code = jsonNode.get("errorType").asText();
            	message = jsonNode.get("message").asText();
            }else if(jsonNode.has("code")==true){
            	code = jsonNode.get("code").asText();
            	message = jsonNode.get("msg").asText();
            }else if(jsonNode.has("errorCode")==true){
            	code = jsonNode.get("errorCode").asText();
            	message = jsonNode.get("errorMessage").asText();
            }else {
            	code = ex.getStatusCode().toString();
            	message = ex.getResponseBodyAsString();
            }
            ErrorResponse response = new ErrorResponse(code, message);
            return new ResponseEntity<>(response, ex.getStatusCode());

        } catch (JsonProcessingException e) {
            ErrorResponse response = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Internal Server Error!");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            
        }
    }
	
	@ApiOperation(value = "HttpServerErrorException 예외처리", notes = "카카오, 네이버의 외부API 송수신 시 외부 API 에러응답값 전달-상태코드 5xx번대")
	@ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpServerErrorException(HttpServerErrorException ex) {
		log.debug("Unexpected httpserver exception occurred", ex);
        ErrorResponse response = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Internal Server Error!");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            
    }
	
	@ApiOperation(value = "MissingServletRequestParameterException 예외처리", notes = "request parameter가 없을 때 예외처리")
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
		log.debug("Unexpected requestParam exception occurred", ex);
		String parameterName = ex.getParameterName();
	    String message = String.format("Required request parameter '%s' for method parameter type %s is not present", parameterName, ex.getParameterType());
	    ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), message);
	    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ApiOperation(value = "IllegalArgumentException 예외처리", notes = "잘못된 입력값 발생 시 예외처리")
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
	    log.debug("Unexpected IllegalArg exception occurred", ex);
	    String message = String.format("잘못된 요청입니다(IllegalArgumentException) : ", ex.getMessage());
	    ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), message);
	    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ApiOperation(value = "MethodArgumentNotValidException 예외처리", notes = "valid check 예외처리")
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
	    log.debug("Unexpected MethodArg exception occurred", ex);
	    String message = String.format("잘못된 요청입니다(MethodArgumentNotValidException) : ", ex.getBindingResult().getFieldError().getDefaultMessage());
	    ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), message);
	    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ApiOperation(value = "NullPointerException 예외처리", notes = "null값 객체 호출 시 예외처리")
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex) {
	    log.debug("Unexpected NullPoint exception occurred", ex);
	    String message = String.format("잘못된 요청입니다(NullPointerException) : ", ex.getMessage());
	    ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(), message);
	    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ApiOperation(value = "NoHandlerFoundException 예외처리", notes = "잘못된 url 호출 시 예외처리")
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException ex) {
	    log.debug("Unexpected NoHandlerFound exception occurred", ex);
	    String message = String.format("잘못된 요청입니다(NoHandlerFoundException) : ", ex.getMessage());
	    ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.toString(), message);
	    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	

}
