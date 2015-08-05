(defproject clj-weblog-analyzer "0.1.0-SNAPSHOT"
  :description "simple clojure weblog analyzer"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :source-paths ["src"]
  :main weblog-analyzer.core
  :aot [weblog-analyzer.core])
