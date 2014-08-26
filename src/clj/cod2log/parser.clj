(ns cod2log.parser
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as string]))

(defn ^:private get-parts-by-name [parts names-with-indicies]
  (zipmap (keys names-with-indicies) (map #(get parts %) (vals names-with-indicies))))

(def ^:private kd-line-parts {:type 0
                              :victim-team 3
                              :victim-name 4
                              :attacker-team 7
                              :attacker-name 8
                              :weapon-name 9
                              :weapon-type 11
                              :weapon-damage 10
                              :weapon-location 12})
(def ^:private init-line-parts {:type 0
                                :map 9})

(def ^:private line-delimiter-regex #"(;|:|\\)")


(defn line-data [line]
  (let [data-parts (string/split line line-delimiter-regex)
        first-word (first data-parts)]
    (cond (= first-word "K") (get-parts-by-name data-parts kd-line-parts)
          (= first-word "D") (get-parts-by-name data-parts kd-line-parts)
          (= first-word "InitGame") (get-parts-by-name data-parts init-line-parts)
          :else {:type nil})))

(defn line-time-and-data [line]
  (let [line-parts (string/split line #"\s" 2)
        time (first line-parts)
        data (line-data (first (rest line-parts)))]
    (conj {:time time} data)))

(defn parse-lines [rdr]
  (for [line (line-seq rdr)]
    (line-time-and-data line)))

;; (defn logfile-to-map [filename]
;;   (with-open [rdr (io/reader filename)]
;;     (parse-lines rdr)))
