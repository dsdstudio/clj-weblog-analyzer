(ns weblog-analyzer.util
  (:require [clojure.tools.reader.edn]))

(defn notnil? [x] (not (nil? x)))
(defn dict-inc [m coll] (update-in m [coll] (fnil inc 0)))
(defn to-datetime [s pattern]
  (-> (java.text.SimpleDateFormat. pattern java.util.Locale/ENGLISH)
      (.parse s)
      (.getTime)))
(defn datetime-to-str [t pattern]
  (.format (java.text.SimpleDateFormat. pattern java.util.Locale/ENGLISH) (java.util.Date. t)))

(defn load-config [file] 
  (clojure.tools.reader.edn/read-string (slurp file)))
