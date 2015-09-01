(ns weblog-analyzer.core
  (:require [clojure.tools.logging :as log]
            [weblog-analyzer.util :refer :all]
            [clojure.data.json :as json]
            [clojure.edn :refer :all])
  (:import java.util.Date)
  (:gen-class))

;웹로그를 담을 데이터구조
(defrecord Weblog 
  [datetime domain ip user method path protocol status size referer ua response_time cookie set-cookie upstream_addr upstream_cache_status upstream_response_time])

(def conf (clojure.edn/read-string (slurp "conf.clj")))

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

(defn group-by-day [coll]
  (for [m (group-by (fn [x] (datetime-to-str (:datetime x) "yyyyMMdd")) coll)]
    {:date (key m) :data (val m)}))
(defn group-by-hour [coll]
  (for [m (group-by (fn [x] (datetime-to-str (:datetime x) "yyyyMMddHH")) coll)]
    {:date (key m) :data (val m)}))

(defn group-by-url [coll]
  (for [m (group-by (fn [x] (:path x)) coll)]
    {:path (key m) :count (count (val m))}))

(defn group-by-useragent [coll]
  (for [m (group-by (fn [x] (:ua x)) coll)]
    {:ua (key m) :count (count (val m))}))

(defn group-by-referer [coll]
  (for [m (group-by (fn [x] (:referer x)) coll)]
    {:referer (key m) :count (count (val m))}))


(defn group-by-campaign-mail [coll]
  (for [m (group-by (fn [x] (datetime-to-str (:datetime x) "yyyyMMddHH")) (filter (fn [x] (re-find #"\/\?ref=email" (:path x))) coll))]
    {:datetime (key m)
     :count (count (val m))
     :data (map (fn [x] {:datetime (datetime-to-str (:datetime x) "yyyy-MM-dd HH:mm:ss")
                         :path (:path x)}) (val m))}))

(defn write-email-analysis-data-to-json [coll]
  (with-open [w (clojure.java.io/writer "email_data.json")]
    (binding [*out* w]
      (json/print-json (sort-by :datetime (group-by-campaign coll))))))

(defn -main [& args]
  (if (empty? args) (println "Usage: java -jar anl.jar [directorypath]")))
