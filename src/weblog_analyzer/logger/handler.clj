(ns weblog-analyzer.logger.handler 
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [weblog-analyzer.analyzer.core :as anl]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defrecord Log [bzid req refefer fv sid ua])
(defroutes app-routes
  (GET "/logging" {params :params} (str params))
  (GET "/data" [request] 
       (apply 
         str 
         (anl/group-by-campaign-email-open
           (anl/log-scan "email_logs"))))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
