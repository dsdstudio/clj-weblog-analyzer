(ns weblog-analyzer.core
  (:gen-class))

(defn scan-log 
  "디렉토리이름으로 로그를 스캔한다
  file-seq"
  [dirname]
  (filter 
    #(false? (.isDirectory %))
     (file-seq (clojure.java.io/file dirname))))

(defn gzfile? 
  [f] 
  (.endsWith ".gz" (.getAbsolutePath f)))

(defn file-to-lineseq
  "파일을 읽어 line-sequence 로 변환"
  [file]
  (line-seq (clojure.java.io/reader file)))

(defn gzipfile-to-lineseq
  "gz 파일을 읽어 line-sequence 로 변환"
  [file]
  (clojure.string/split-lines 
    (with-open
      [in (java.util.zip.GZIPInputStream. (clojure.java.io/input-stream file))]
      (slurp in))))

(defn serialize-log 
  "로그라인을 tokenize한다"
  ; ip, uid, userid, time, request, status, bytes-sent, referer-url, user-agent
  [log]
    ; keys 와 tokenized-log sequence(vals) 를 교차되게 엮어서 새로운 hash-map을 만들어낸다
  (let 
    [m (zipmap [:ip :uid :userid :datetime :request :status :bytes-sent :referer-url :user-agent]
    ; TODO 정규식이 잘못된 탓인지 sequence 안에 re-seq 가 들어있는 형태의 자료구조가 나온다 -_-; 수정 필요.. (re-seq ... ) 
    (rest 
      (first 
        (re-seq #"^([\d.]+)\ (\S+)\ (\S+)\ \[([\w:/]+\s[+\-]\d{4})\]\ \"(.+?)\"\ (\d{3})\ (\d+)\ \"([^\"]+)\"\ \"([^\"]+)\"" log))))]
    (assoc m 
      :datetime (.parse (java.text.SimpleDateFormat. "[dd/MMM/yyyy:HH:mm:ss ZZZZ]" java.util.Locale/ENGLISH) (get m :datetime)))))

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
    (log-scan (first args))))
