# ChocoMap
> 장소 검색 OPEN API 서비스 프로그램  


카카오 장소검색 API와 네이버 장소 검색 API를 사용하여 키워드 별 장소를 검색할 수 있습니다.<br/>
가장 많이 조회한 상위 10개 검색 키워드 목록을 제공합니다.


<br/>

## :dart: Content

- [🛠 Built With](#-built-with)
- [📖 API 규격](#-api-규격)
    + [키워드 검색 API](#키워드-검색-api)
    + [상위 키워드 조회수 API](#상위-키워드-조회수-api)
  * [:star2: 주요기능](#star2-주요기능)
  * [:gem: 아키텍쳐](#gem-아키텍쳐)
  * [🚀 클래스 설명](#-클래스-설명)
  * [:triangular_flag_on_post: 테스트 방법](#triangular_flag_on_post-테스트-방법)

<br/>

## 🛠 Built With

<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white"> 
<img src="https://img.shields.io/badge/Spring Boot-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white"> 
<img src="https://img.shields.io/badge/h2 database-007396?style=for-the-badge&logo=h2 database&logoColor=white"> 

<br/>

## 📖 API 규격

<br/>

###  키워드 검색 API 




- 기본 정보

|    API ID    |   API 명 (URI)   | HTTP Method |                        설명                       | Content-Type (요청) |       Content-Type (응답)       |
|:------------:|:----------------:|:-----------:|:-------------------------------------------------:|:-------------------:|:-------------------------------:|
| 장소검색 API | /search?keyword= |     GET     | 키워드 검색 API로 카카오, 네이버 검색 데이터 제공 |          -          | application/json; charset=UTF-8 |


- 요청메세지 명세

|    HTTP   | 항목명(카카오) | 항목명(네이버) |    항목설명    | 필수 | 타입 |
|:---------:|----------------|:--------------:|:--------------:|:----:|:----------:|
| Parameter | query | query | 검색을 원하는 질의어 |   Y  |   String   |


- 응답메세지 명세

|    HTTP   | 항목명(카카오) | 항목명(네이버) |    항목설명    | 필수 | 타입 |
|:---------:|----------------|:--------------:|:--------------:|:----:|:----------:|
| Body      | place_tot_count| place_tot_count| 검색된 총 갯수 (카카오,네이버 전체) |   Y  |   String   |
| Body      | listPlace     |      listPlace     | 검색된 리스트 (카카오,네이버 전체) |   Y  |   String   |
| Body      | --place_name     |      title     | 장소명, 업체명 |   N  |   String   |
| Body      | --category_name  |      category  | 카테고리 이름  |   N  |   String   |
| Body      | --phone          |     telephone  | 전화번호       |   N  |   String   |
| Body      | --address_name   |     address    | 전체 지번 주소 |   N  |   String   |
| Body      | --road_address_name|     roadAddress    | 전체 도로명 주소 |   N  |   String   |
| Body      | --place_url      |     link    | 장소 상세페이지 URL |   N  |   String   |


<br/>

 ###  상위 키워드 조회수 API 



- 기본 정보

|     API ID     | API 명 (URI) | HTTP Method |                   설명                  | Content-Type (요청) |       Content-Type (응답)       |
|:--------------:|:------------:|:-----------:|:---------------------------------------:|:-------------------:|:-------------------------------:|
| 상위검색어 API |   /topList   |     GET     | 조회수 기반으로 상위 10개의 검색어 조회 |          -          | application/json; charset=UTF-8 |


- 요청메세지 명세

|    HTTP   | 항목명 |    항목설명    | 필수 | 타입 |
|:---------:|:----------------:|:--------------:|:----:|:----------:|
| Parameter | -   | - |   -  |   -   |


- 응답메세지 명세

|    HTTP   | 항목명 |    항목설명    | 필수 | 타입 |
|:---------:|:----------------:|:--------------:|:----:|:----------:|
| Body      | keyword   | 검색어 |   N  |   String   |
| Body      | view_cnt  | 조회수 |   N  |   Integer   |



<br/>

## :star2: 주요기능

1. 장소 검색 기능
   > 외부 API(카카오와 네이버)를 사용하여 클라이언트가 입력한 키워드로 장소를 검색합니다. 
     카카오 API를 우선검색 후, 네이버 API로 검색합니다. (각 최대 5개이며, 총 10개까지 검색이 가능합니다.)
     중복되는 결과가 있을 경우 유사도 체크를 통해 중복을 제거합니다.

2. 상위 검색어 조회 기능
   > 데이터베이스에 저장된 검색어와 조회수를 기반으로 상위 10개의 검색어 목록을 제공합니다.

3. 트래픽 제한 기능
   > rate limit으로 Bucket4j 라이브러리를 사용하여 API 호출 횟수를 제한합니다. 
     테스트를 위해 요청 처리 용량은 10개로 설정되어 있고, 20초마다 10개의 요청량이 재생성됩니다.
     요청값 설정은 application.properties값에서 사용하기 때문에 대용량 API 호출 시에도 설정값을 쉽게 변경하여 추후 유지보수에 용이하게 하였습니다.

3. 예외 처리 기능
   > 각종 예외 상황에 대해 공통 응답폼을 사용해 6.에러 코드와 메세지를 응답 처리합니다. 

4. API UI 테스트 기능
   > Swagger를 사용하여 API에 대한 문서화를 제공하고, UI를 통해 테스트를 진행할 수 있습니다.

5. 확장성 및 유지보수성
   > 프로퍼티 파일을 사용하여 설정값을 변경하고, 빈 클래스를 사용하여 각 API에 대한 필드와 플래그를 관리하여 추후 API 확장에 대비합니다.

6. 동시성 제어
   > 검색한 키워드에 lock을 걸어 데이터베이스에서 동시성 문제를 방지하고자 하였습니다.

7. 단위 테스트
   > @SpringBootTest를 사용하여 주요 기능에 대한 단위 테스트를 생성해 프로젝트의 안정성을 높고자 하였습니다.



<br/>

## :gem: 아키텍쳐
```bash
📦src
 ┣ 📂main
 ┃ ┣ 📂java
 ┃ ┃ ┗ 📂com
 ┃ ┃   ┗ 📂choco
 ┃ ┃     ┗ 📂chocomap
 ┃ ┃       ┣ 📂config                              // h2 Database 연결설정 (application.properties에서 설정값 참조)
 ┃ ┃       ┃ ┗ 📜DatabaseConfig.java
 ┃ ┃       ┣ 📂searchmap
 ┃ ┃       ┃ ┣ 📂common                           // API 송수신시 사용되는 공통 클래스 구성
 ┃ ┃       ┃ ┃ ┣ 📂exceptions                     // API 에러 발생 시 전역 예외 처리
 ┃ ┃       ┃ ┃ ┃ ┗ 📜ApiExceptionHandler.java
 ┃ ┃       ┃ ┃ ┣ 📂ratelimiting                   // REST API 호출수(엑세스) 제한 설정
 ┃ ┃       ┃ ┃ ┃ ┣ 📜AppRateLimitingConfig.java
 ┃ ┃       ┃ ┃ ┃ ┣ 📜RateLimitingFilter.java
 ┃ ┃       ┃ ┃ ┃ ┗ 📜RateLimitingProperties.java
 ┃ ┃       ┃ ┃ ┣ 📂swagger                        // API UI 테스트용 Swagger 연동
 ┃ ┃       ┃ ┃ ┃ ┗ 📜SwaggerConfig.java
 ┃ ┃       ┃ ┃ ┣ 📜ApiFieldForm.java
 ┃ ┃       ┃ ┃ ┣ 📜ApiFlagForm.java
 ┃ ┃       ┃ ┃ ┣ 📜ApiProperties.java
 ┃ ┃       ┃ ┃ ┗ 📜ErrorResponse.java
 ┃ ┃       ┃ ┣ 📂Controller                        // Spring MVC 컨트롤러  
 ┃ ┃       ┃ ┃ ┗ 📜SearchMapController.java
 ┃ ┃       ┃ ┣ 📂Dao                               // Spring MVC DAO
 ┃ ┃       ┃ ┃ ┗ 📜SearchMapDao.java
 ┃ ┃       ┃ ┣ 📂Dto                               // Spring MVC DTO
 ┃ ┃       ┃ ┃ ┣ 📜KeywordDto.java
 ┃ ┃       ┃ ┃ ┣ 📜PlaceDto.java
 ┃ ┃       ┃ ┃ ┗ 📜SearchListPlaceDto.java
 ┃ ┃       ┃ ┣ 📂infrastructure
 ┃ ┃       ┃ ┃ ┗ 📂mybatis
 ┃ ┃       ┃ ┃   ┗ 📂mapper                       // Mapper 인터페이스
 ┃ ┃       ┃ ┃     ┗ 📜SearchKeywordMapper.java
 ┃ ┃       ┃ ┗ 📂Service                          // Spring 서비스
 ┃ ┃       ┃   ┗ 📜SearchMapService.java
 ┃ ┃       ┗ 📜ChocomapApplication.java
 ┃ ┗ 📂resources
 ┃   ┣ 📂mappers
 ┃   ┃ ┗ 📂searchmap                               // 쿼리 등록
 ┃   ┃   ┗ 📜SearchKeywordMapper.xml
 ┃   ┣ 📜application.properties                    // 설정값 등록
 ┃   ┗ 📜schema.sql
 ┗ 📂test
   ┗ 📂java
     ┗ 📂com
       ┗ 📂choco
         ┗ 📂chocomap
           ┣ 📂test
           ┃ ┗ 📂service                           // 단위테스트 클래스
           ┃   ┣ 📜RateLimitingFilterTest.java
           ┃   ┗ 📜SearchMapServiceTest.java
           ┗ 📜ChocomapApplicationTests.java  
``` 


<br/>

 ## 🚀 클래스 설명
 
```sh

* ApiExceptionHandler.java
  → @ExceptionHandler(HttpClientErrorException.class) : 
    카카오, 네이버의 외부API 송수신 시 외부 API응답값을 클라이언트에게 전달하기 위해 사용 (HTTP status 4xx번대 예외처리)
  → @ExceptionHandler(HttpServerErrorException.class) : 
    카카오, 네이버의 외부API 송수신 시 외부 API응답값을 클라이언트에게 전달하기 위해 사용 (HTTP status 5xx번대 예외처리)
  → @ExceptionHandler(MissingServletRequestParameterException.class) : 
    request parameter가 없을 때 예외처리
  → @ExceptionHandler(IllegalArgumentException.class) : 
    잘못된 입력값 발생 시 예외처리
  → @ExceptionHandler(MethodArgumentNotValidException.class) : 
    valid check 예외처리
  → @ExceptionHandler(NullPointerException.class) : 
    null값 객체 호출 시 예외처리
  → @ExceptionHandler(NoHandlerFoundException.class) : 
    잘못된 url 호출 시 예외처리
    
* AppRateLimitingConfig.java
  → 필터 활성화 클래스
  
* RateLimitingFilter.java
  → 트래픽 제한 기능
    API 호출횟수를 제한하기 위해 Bucket4j 라이브러리 사용 
    request에 있는 ip주소를 가져와 Bucket에 담아주고 요청처리 용량은 10, 20초마다 10개의 요청량 재생성 설정
    버킷의 총량을 넘어갈 경우 에러공통코드로 에러코드, 에러메세지 응답
    application.properties에 있는 값 바인딩 시켜 Bandwidth 설정값 유지보수 향상
    API 요청 당 한 번만 적용되기 위해 OncePerRequestFilter 상속 (필터 중복방지)
    
* RateLimitingProperties.java
  → rate limit 설정에 사용할 바인딩 빈클래스
  
* SwaggerConfig.java
  → API UI로 테스트하기 위한 Swagger 연동클래스
  
* ApiFieldForm.java
  → OPEN API마다 규격 response Key값 매핑을 위한 빈클래스 (추가 API등록 시 확장성)
  
* ApiFlagForm.java
  → Service단에서 반복성 코드(조건값) 감소를 위한 데이터추출 enum클래스
  
* ApiProperties.java
  → API 송수신 설정 키값 매핑 빈클래스 (application.properties 참조)
    
* ErrorResponse.java
  → API 공통된 에러응답값 매핑 빈클래스
  
* SearchMapController.java
  → 컨트롤러 클래스
    /search (장소검색, keyword 파라미터 사용), /topList (상위 조회수) url 사용
    
* SearchMapService.java
  → 서비스 클래스
    API에서 받아온 JSON 응답값을 PlaceDto로 받아 응답
    카카오 검색어 우선, 네이버 검색 후 카카오 검색어와 중복 발생 시 유사도(장소명과 주소 결합) 체크하여 중복제거 (유사도 80%이상)
    네이버 검색결과 중복 되었을때 중복된 검색어 갯수만큼 추가로 응답값 가져오려 했지만 
    네이버에서 제공한 API는 페이지네이션이 없이 start 1로 고정되어 있어 최대 5개 응답값으로 고정됨
    네이버 페이지네이션 관련해서 추후 변경될 사항에 대비해 start값 변수로 받아 API송신하는 로직도 추가 
    동시성 해결을 위해 lockKeyword 메서드 실행 후 insertKeyword 메서드 실행

* SearchMapDao.java
  → DAO 클래스
  
* KeywordDto.java
  → DTO 클래스
    상위검색어 조회시 사용(조회수, 키워드 컬럼)
    
* PlaceDto.java
  → DTO 클래스
    장소검색 조회시 사용(API 응답값 공통컬럼으로 사용 - 장소명, 도로명주소, 주소, 카테고리명, 전화번호, URL, 검색API명 컬럼)
    
* SearchListPlaceDto
  → DTO 클래스
    PlaceDto를 List로 변환 시 사용(PlaceDto 리스트, 검색된 총 갯수 컬럼)
    
* SearchKeywordMapper.java
  → Mapper 인터페이스
    
* SearchKeywordMapper.xml
  → 장소 검색어 테이블에 insert (merge사용-update,insert), 상위 10개 검색어 조회
    검색한 키워드를 lock을 걸어 동시성 이슈 제거
  
* RateLimitingFilterTest.java 
  → Rate limit 초과발생 단위테스트
  
* SearchMapServiceTest.java 
  → 장소검색 API, 상위검색어 메소드 단위테스트
  
```
 
<br/> 
 

## :triangular_flag_on_post: 테스트 방법

< Swagger 테스트 방법 >

  1. SpringBoot local 서버 실행

  2. 주소창에 http://localhost:8081/swagger-ui.html 실행 


  <img src="https://user-images.githubusercontent.com/128205880/226826602-d8b3b3bd-531b-4d68-90fd-e511d7616f73.PNG" width="700" height="400"/>

  3. search-map-controller 클릭 → 하단에 테스트 가능한 URL 선택
    
   - /search 메서드 클릭 → Try it out 버튼 클릭


   <img src="https://user-images.githubusercontent.com/128205880/226826776-0cf68efc-aaf7-4ec7-a2a3-d11b7deb8fb1.PNG" width="700" height="400"/>
     
   - 장소(검색) 키워드 input값에 검색하고자 하는 키워드 입력 (예시: 마트)


   <img src="https://user-images.githubusercontent.com/128205880/226826813-89c9f7fe-9fbc-4ad6-ac79-e7d916a51485.PNG" width="700" height="400"/>
      
   - Response Body와 Http status code, Response Header 확인 가능


   <img src="https://user-images.githubusercontent.com/128205880/226826841-c9b0c245-2b75-4145-9cb8-2b99bdff249f.PNG" width="700" height="400"/>
      
< curl 테스트 방법 >

  1. cmd 실행
  
  2. curl  http://localhost:8081/search?keyword= 검색할 단어 입력 후 엔터 (예시:  http://localhost:8081/search?keyword=apple) 


   <img src="https://user-images.githubusercontent.com/128205880/226835509-786e989b-0255-48ed-88a1-00272e6c77c1.PNG" width="1000" height="400"/>

      
< POSTMAN 테스트 방법 >
  
  1. POSTMAN 설치 후 실행

  2. GET 방식 설정 후 URL란에 http://localhost:8081/search?keyword= 검색할 단어 입력 후 SEND 버튼 클릭 → 응답값 확인 (예시: http://localhost:8081/search?keyword=포비)


     <img src="https://user-images.githubusercontent.com/128205880/226827009-512c5be7-fb02-433d-a652-9ed889513914.PNG" width="800" height="500"/>
    














