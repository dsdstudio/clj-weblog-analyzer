(ns weblog_analyzer.handler_test 
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [weblog-analyzer.logger.handler :refer :all]))

(deftest test-app
  (testing "logging route"
    (let [response (app (mock/request :get "/logging?a=1"))]
      (is (= (:status response) 200))
      (is (= (:body response) (str {:status 200 :msg ""}))))))
