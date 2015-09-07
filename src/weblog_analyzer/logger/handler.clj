(ns weblog-analyzer.logger.handler 
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defrecord Log [bzid, req, refefer, fv, sid, ua])
(defroutes app-routes
  (GET "/logging" request
       ; TODO logging 처리
       (str {:status 200 :msg ""}))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
