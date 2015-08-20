(ns weblog-analyzer.core-test
  (:require [clojure.test :refer :all]
            [weblog-analyzer.core :refer :all]))

(def invalid-log "54.163.170.62 - - [05/Aug/2015:19:52:18 +0900] \"\" 400 0 \"-\" \"-\"")
(def log-string 
  "218.51.23.36 - - [25/Jul/2015:00:23:24 +0900] \"GET /api/pethostel/latelyjoin HTTP/1.1\" 200 1844 \"https://www.pethostel.net/\" \"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)\"")
(def compare-log-map 
  {:ip "218.51.23.36", 
   :uid "-", 
   :userid "-", 
   :datetime (-> (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm:ss" java.util.Locale/ENGLISH)
                 (.parse "2015-07-25 00:23:24")
                 (.getTime))
   :request "GET /api/pethostel/latelyjoin HTTP/1.1", 
   :status "200", 
   :bytes-sent "1844", 
   :referer-url "https://www.pethostel.net/", 
   :user-agent "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)"})

(deftest serialize-log-test  
  "로그라인 직렬화 처리를 검사한다"
  (is (.equals compare-log-map (weblog-analyzer.core/serialize-log log-string))))

(deftest invalid-log-test 
  "잘못된 로그를 검사한다."
  (is (nil? (serialize-log invalid-log))))
