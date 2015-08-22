(ns weblog-analyzer.util)

(defn notnil? [x] (not (nil? x)))

(defn dict-inc [m coll] (update-in m [coll] (fnil inc 0)))

(defn to-datetime [s pattern]
  (-> (java.text.SimpleDateFormat. pattern java.util.Locale/ENGLISH)
      (.parse s)
      (.getTime)))
