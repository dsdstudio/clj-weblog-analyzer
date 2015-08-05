# weblog-analyzer

clojure study 용으로 작은 웹로그 분석기를 만든다.

## Usage

	java -jar webloganl.jar [logfilepath]

## TODO 

- 파일디렉토리에서 로그파일을 읽는다 O
  - 로그파일을 line by line 으로 읽고 이것을 처리하기쉬운 map 형태의 데이터구조로 만든다. O
- 로그 필터링 설정
  - 로그파일 필터링 설정할 수 있도록 
  - url 패턴 매칭
- 일별 url 통계를 뽑아낸다.
- useragent map 만들기

## Feature 0.0.1 

- 로그 필터링 설정 
- url 패턴 필터링 설정
- 일별 url 통계

## License

Copyright © 2015 Bohyung kim
