(defproject weblog-analyzer "0.1.0-SNAPSHOT"
  :description "simple clojure weblog analyzer"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/data.json "0.2.6"]
                 [compojure "1.4.0"]
                 [com.stuartsierra/component "0.2.3"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring-server "0.3.1"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [com.novemberain/monger "3.0.0"]
                 [ch.qos.logback/logback-classic "1.1.3"]
                 [cheshire "5.5.0"]]
  :plugins [[lein-ring "0.9.6"]
            [lein-sub "0.3.0"]]
  :ring {:handler weblog-analyzer.logger.handler/app}
  :profiles {:dev {:dependencies [[javax.servlet/servlet-api "2.5"] 
                                  [ring-mock "0.1.5"]]}}
  :source-paths ["src"]
  :main weblog-analyzer.analyzer.core
  :aot [weblog-analyzer.analyzer.core])
