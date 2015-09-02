(ns weblog-analyzer.core-test
  (:require [clojure.test :refer :all]
            [weblog-analyzer.core :refer :all]))

(def sample-cookie "JSESSIONID=1BBC8036F143CE24A3497C4BE810811F; _ga=GA1.2.647868696.1441091332; _gat=1; _hjIncludedInSample=1; _hjUserId=fa0b20ba-0093-4cce-aa77-2e911638f763; X-PHAuthorization=v_46XkF2TeaXCsRbWBOQjg; PH-User=%7B%22sessionId%22%3A%22v_46XkF2TeaXCsRbWBOQjg%22%2C%22userId%22%3A%22X32BdlK7Rgq65Ah3ZDhw-Q%22%2C%22userEmail%22%3A%22ultrabar%40naver.com%22%2C%22userName%22%3A%22%ED%99%8D%EA%B0%80%ED%9D%AC%22%2C%22userImage%22%3A%7B%22imageKey%22%3A%22user%2F20150901%2F16%2FnVqT35SRSo6BFX350kLBNg%22%7D%2C%22hasHostel%22%3Afalse%2C%22isActiveHostel%22%3Afalse%2C%22finishCostSetup%22%3Afalse%2C%22remainDaysLastWorkingDay%22%3A0%7D")
(def empty-cookie "-")
(def null-cookie "null")

(deftest empty-cookie-test
  "빈 쿠키 테스트"
  (is (= ["-"] (parse-cookie empty-cookie))))

(deftest null-cookie-test 
  "빈 쿠키 테스트"
  (is (= ["null"] (parse-cookie null-cookie))))

(deftest cookie-test 
  "테스트"
  (println (parse-cookie sample-cookie))
  (is (= 0 0)))
