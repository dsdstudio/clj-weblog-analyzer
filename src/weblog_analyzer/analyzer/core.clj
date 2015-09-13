(ns weblog-analyzer.analyzer.core
  (:require [clojure.tools.logging :as log]
            [weblog-analyzer.util :refer :all]
            [clojure.data.json :as json]
            [clojure.edn :as edn])
  (:use clojure.pprint)
  (:import java.util.Date)
  (:gen-class))

;웹로그를 담을 데이터구조
(defrecord Weblog 
  [datetime domain ip user method path protocol status size referer ua response_time cookie set-cookie upstream_addr upstream_cache_status upstream_response_time])

(def conf (edn/read-string (slurp "conf.clj")))
(defn scan-directory
  "디렉토리이름으로 로그를 스캔한다"
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
(defn parse-cookie [cookie-str]
  (into {}
    (for [#^String cookie (.split cookie-str ";")]
      (let [keyval (map (fn [#^String x] (.trim x)) (.split cookie "=" 2))]
        [(first keyval) (second keyval)]))))

(defn parse-log 
  "로그라인을 파싱한다"
  [log]
  (let [weblog
        (-> (apply ->Weblog (tokenize-weblog log))
        (update-in [:datetime] #(to-datetime % "dd/MMM/yyyy:HH:mm:ss ZZZ"))
        (update-in [:cookie] #(parse-cookie %)))]
    (assoc weblog :params (param-map (:path weblog)))))

(defn log-scan [file]
   (mapcat
     #(if (gzfile? %)
        (filter notnil? (map parse-log (gzip-read-lines %)))
        (filter notnil? (map parse-log (read-lines %))))
     (scan-directory file)))

(defn group-by-campaign-mail [coll]
  (sort 
    #(compare (:datetime %1) (:datetime %2)) 
    (for [m (group-by (fn [x] (datetime-to-str (:datetime x) "yyyyMMddHH")) (filter (fn [x] (re-find #"\/\?ref=email" (:path x))) coll))]
    {:datetime (key m)
     :count (count (val m))
     :data (map (fn [x] {
                         :datetime (datetime-to-str (:datetime x) "yyyy-MM-dd HH:mm:ss")
                         :path (:path x)}) (val m))})))

(defn group-by-campaign-email-open "email open 통계를 계산한다" [coll]
  (sort 
    #(compare (:datetime %1) (:datetime %2))
    (for [m (group-by
              (fn [x] (datetime-to-str (:datetime x) "yyyyMMddHH"))
              (filter (fn [x] (not (contains? (:filtered-ip conf) (:ip x)))) coll))]
    {:datetime (key m)
     :count (count (val m))
     :data (map (fn [x] {
                         :datetime (datetime-to-str (:datetime x) "yyyyMMddHHmmss")
                         :path (:path x)}) (val m))})))

(defn write-email-analysis-data-to-json [coll]
  (with-open [w (clojure.java.io/writer "email_data.json")]
    (binding [*out* w]
      (json/print-json coll))))

(defn email-open-stat-by-distinct [coll]
  (doall 
    (map pprint (distinct (map :path coll)))))
