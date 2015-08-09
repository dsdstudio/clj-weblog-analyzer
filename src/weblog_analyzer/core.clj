(ns weblog-analyzer.core
  (:require [clojure.tools.logging :as log])
  (:gen-class))

;웹로그를 담을 데이터구조
(defrecord Weblog 
  [ip uid userid datetime request status bytes-sent referer-url user-agent])

(defn scan-log 
  "디렉토리이름으로 로그를 스캔한다
  file-seq"
  [dirname]
  (log/info "scanning directory : " dirname)
  (filter 
    #(false? (.isDirectory %))
     (file-seq (clojure.java.io/file dirname))))

(defn gzfile? 
  [f] 
  (.endsWith ".gz" (.getAbsolutePath f)))

(defn file-to-lineseq
  "파일을 읽어 line-sequence 로 변환"
  [file]
  (log/info "file read : " file)
  (line-seq (clojure.java.io/reader file)))

(defn gzipfile-to-lineseq
  "gz 파일을 읽어 line-sequence 로 변환"
  [file]
  (log/info "gz file read : " file)
  (clojure.string/split-lines 
    (with-open
      [in (java.util.zip.GZIPInputStream. (clojure.java.io/input-stream file))]
      (slurp in))))

(defn tokenize
  ; TODO 정규식이 잘못된 탓인지 sequence 안에 re-seq 가 들어있는 형태의 자료구조가 나온다 -_-; 수정 필요.. (re-seq ... ) 
  [log]
  (rest 
    (first 
      (re-seq #"^([\d.]+)\ (\S+)\ (\S+)\ \[([\w:/]+\s[+\-]\d{4})\]\ \"(.+?)\"\ (\d{3})\ (\d+)\ \"([^\"]+)\"\ \"([^\"]+)\"" log))))


(defn filter-log 
  "정규식에 맞지않는형식의 로그는 필터링한다"
  [coll] 
  (filter (fn [x] (not-empty x)) coll))

(defn objectfy-data [coll] 
  "seq 형태의 자료구조를 가지고 weblog defrecord 형태로 변환"
  (map #(apply (Weblog. %1 %2 %3 %4 %5 %6 %7 %8 %9) coll)))

(defn serialize-log
  "로그라인을 tokenize한다"
  [log]
  (log/debug log)
  ; process pipeline 
  ; TODO assoc datetime 처리 추가
  ; TODO 좀더 깔끔한 이디엄은 없나 ?
  (objectfy-data (filter-log (tokenize log))))

  ;(assoc m
  ;  :datetime (.parse (java.text.SimpleDateFormat. "dd/MMM/yyyy:HH:mm:ss ZZZ" java.util.Locale/ENGLISH) datetime)))

(defn log-scan
  [file]
  (mapcat 
    ;gz 파일인경우 읽는방식이 다르므로 별도로 line-seq 읽는 함수를 만들었다.
    #(if (gzfile? %) 
       (map serialize-log (gzipfile-to-lineseq %)) 
       (map serialize-log (file-to-lineseq %)))
    (scan-log file)))

(defn success-request? 
  [log]
  (= "200" (get log :status)))

; function literal (fn [x] (get x :ip)) -> #(get % :ip)
; (count (distinct (map #(get % :ip) (filter success-request? (log-scan "logs")))))

(defn -main [& args]
  (if (empty? args) (println "Usage: java -jar anl.jar [directorypath]")
    (let [x (log-scan (first args))]
      (println (count x)))))
