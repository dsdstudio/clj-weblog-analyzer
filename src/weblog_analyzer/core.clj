(ns weblog-analyzer.core
  (:require [clojure.tools.logging :as log])
  (:gen-class))

;웹로그를 담을 데이터구조
(defrecord Weblog 
  [ip uid userid datetime request status bytes-sent referer-url user-agent])

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

;TODO 유틸성 함수들 외부파일로 뺴는게 좋을듯
(defn notnil? [x] (not (nil? x)))
(defn dict-inc [m coll] (update-in m [coll] (fnil inc 0)))
(defn to-datetime [s pattern]
  (-> (java.text.SimpleDateFormat. pattern java.util.Locale/ENGLISH)
      (.parse s)
      (.getTime)))

(defn tokenize-weblog 
  [log] 
  (-> (re-seq #"^([\d.]+)\ (\S+)\ (\S+)\ \[([\w:/]+\s[+\-]\d{4})\]\ \"(.+?)\"\ (\d{3})\ (\d+)\ \"([^\"]+)\"\ \"([^\"]+)\"" log)
      first
      rest))


(defn parse-log 
  "로그라인을 파싱한다"
  [log]
  (let [tokens (tokenize-weblog log)]
    (if (= (count tokens) 9) (update-in (apply ->Weblog tokens) [:datetime] #(to-datetime % "dd/MMM/yyyy:HH:mm:ss ZZZ"))
      nil)))

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
  (reduce dict-inc {} (map #(get % :ip) coll)))

(defn referer-stat 
  "referer별 통계를 뽑아낸다"
  [coll]
  (reduce dict-inc {} (map #(get % :referer-url) coll)))

(defn -main [& args]
  (if (empty? args) (println "Usage: java -jar anl.jar [directorypath]")
    (doall
      (map println (reverse (sort-by #(last %1) (referer-stat (log-scan (first args)))))))))
