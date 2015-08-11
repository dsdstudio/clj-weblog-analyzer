(defproject weblog-analyzer "0.1.0-SNAPSHOT"
  :description "simple clojure weblog analyzer"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [compojure "1.4.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [ch.qos.logback/logback-classic "1.1.3"]]
  :plugins [[lein-ring "0.9.6"]]
  :source-paths ["src"]
  :main weblog-analyzer.core
  :aot [weblog-analyzer.core])
