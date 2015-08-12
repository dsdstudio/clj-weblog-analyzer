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
  (filter 
    #(false? (.isDirectory %))
     (file-seq (clojure.java.io/file dirname))))

(defn gzfile? 
  "gzfile인지 판단"
  [f] 
  (-> (.getAbsolutePath f)
     (.endsWith ".gz")))

(defn file-to-lineseq
  "파일을 읽어 line-sequence 로 변환"
  [file]
  (log/info "file read : " (.getAbsolutePath file))
  (line-seq (clojure.java.io/reader file)))

(defn gzipfile-to-lineseq
  "gz 파일을 읽어 line-sequence 로 변환"
  [file]
  (log/info "gz file read : " (.getAbsolutePath file))
  (clojure.string/split-lines 
    (with-open
      [in (java.util.zip.GZIPInputStream. (clojure.java.io/input-stream file))]
      (slurp in))))

(defn tokenize
  [log]
  (re-seq #"^([\d.]+)\ (\S+)\ (\S+)\ \[([\w:/]+\s[+\-]\d{4})\]\ \"(.+?)\"\ (\d{3})\ (\d+)\ \"([^\"]+)\"\ \"([^\"]+)\"" log))

(defn serialize-log
  "로그라인을 tokenize한다"
  [log]
  (let [tokens (rest (first (tokenize log)))]
    (if (not= (count tokens) 9) nil
      (let [weblog (apply ->Weblog tokens)]
      (assoc weblog
             :datetime (-> (java.text.SimpleDateFormat. "dd/MMM/yyyy:HH:mm:ss ZZZ" java.util.Locale/ENGLISH)
                           (.parse (get weblog :datetime))
                           (.getTime)))))))
(defn log-scan
  [file]
  (mapcat 
    #(if (gzfile? %) 
       (filter (fn [x] (not (nil? x))) (map (fn [x] (serialize-log x)) (gzipfile-to-lineseq %)))
       (filter (fn [x] (not (nil? x))) (map (fn [x] (serialize-log x)) (file-to-lineseq %))))
    (scan-directory file)))

(defn -main [& args]
  (if (empty? args) (println "Usage: java -jar anl.jar [directorypath]")
    (doall
      (map println (distinct (map #(get % :ip) (log-scan (first args))))))))
