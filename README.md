# weblog-analyzer

WARNING! this is my study(experimental) project.

clojure study 용으로 작은 웹로그 분석기를 만든다.

## Usage

	java -jar webloganl.jar [logfilepath]

## TODO 

- 파일디렉토리에서 로그파일을 읽는다 O
  - 로그파일을 line by line 으로 읽고 이것을 처리하기쉬운 map 형태의 데이터구조로 만든다. O
- 로그 필터링 설정
  - 로그파일 필터링 설정할 수 있도록 O
  - url 패턴 매칭
- url별 요청 통계를 뽑아낸다.
  - url별 일별 요청통계를 뽑아낸다
- 유입채널별 통계를 뽑아낸다.
- 유입채널별 검색어별 통계를 뽑아낸다.
- useragent map 만들기

## Feature 0.0.1

- ip별 통계
- 유입채널별 통계
- 로그 필터링 설정
- url 패턴 필터링 설정

## License

Copyright © 2015 Bohyung kim
