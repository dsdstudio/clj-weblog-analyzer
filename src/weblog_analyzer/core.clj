(ns weblog-analyzer.core
  (:require [clojure.tools.logging :as log]
            [weblog-analyzer.util :refer :all]) 
  (:import java.util.Date)
  (:gen-class))

;웹로그를 담을 데이터구조
(defrecord Weblog 
  [datetime domain ip user method path protocol status size referer ua response_time cookie set-cookie upstream_addr upstream_cache_status upstream_response_time])

(defn scan-directory
  "디렉토리이름으로 로그를 스캔한다
  file-seq"
  [dirname]
  (log/info "scanning directory : " dirname)
  (->> (clojure.java.io/file dirname)
       (file-seq)
       (filter #(false? (.isDirectory %)))))

(defn gzfile? 
  "gzfile인지 판단"
  [f] 
  (-> (.getAbsolutePath f)
      (.endsWith ".gz")))

(defn read-lines
  "파일을 읽어 line-sequence 로 변환"
  [file]
  (log/info "file read : " (.getAbsolutePath file))
  (clojure.string/split-lines (slurp (clojure.java.io/reader file))))

(defn gzip-read-lines 
  "gz 파일을 읽어 line-sequence 로 변환"
  [file]
  (log/info "gz file read : " (.getAbsolutePath file))
  (clojure.string/split-lines 
    (with-open
      [in (java.util.zip.GZIPInputStream. (clojure.java.io/input-stream file))]
      (slurp in))))

(defn tokenize-weblog [log] (clojure.string/split log #"\t"))

(defn parse-log 
  "로그라인을 파싱한다"
  [log]
  (update-in (apply ->Weblog (tokenize-weblog log)) [:datetime] #(to-datetime % "dd/MMM/yyyy:HH:mm:ss ZZZ")))

(defn log-scan [file]
   (mapcat
     #(if (gzfile? %)
        (filter notnil? (map parse-log (gzip-read-lines %)))
        (filter notnil? (map parse-log (read-lines %))))
     (scan-directory file)))

(defn ip-stat 
  "IP별 통계를 뽑아낸다 
  {:ip xxx :count 33} ....."
  [coll]
  (reduce dict-inc {} (map :ip coll)))

(defn url-stat [coll]
  (reduce dict-inc {} (map :request coll)))

(defn referer-stat 
  "referer별 통계를 뽑아낸다"
  [coll]
  (reduce dict-inc {} (map :referer-url coll)))

(defn group-by-day [coll]
  (sort-by first (for [m (group-by (fn [x] (datetime-to-str (:datetime x) "yyyyMMdd")) coll)]
    {:date (key m) :count (count (val m))})))

(defn group-by-url [coll]
  (for [m (group-by (fn [x] (:path x)) coll)]
    {:path (key m) :count (count (val m))}))

(defn group-by-useragent [coll]
  (for [m (group-by (fn [x] (:ua x)) coll)]
    {:ua (key m) :count (count (val m))}))

(defn -main [& args]
  (if (empty? args) (println "Usage: java -jar anl.jar [directorypath]")
    (doall
      (map println (reverse (sort-by #(last %1) (referer-stat (log-scan (first args)))))))))
