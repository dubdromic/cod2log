(ns cod2log.parser
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as string]))

(defn ^:private get-parts-by-name [parts names-with-indicies]
  (zipmap (keys names-with-indicies) (map #(get parts %) (vals names-with-indicies))))

(def ^:private victim-info-parts {:victim-team 3 :victim-name 4})
(def ^:private attacker-info-parts {:attacker-team 7 :attacker-name 8})
(def ^:private weapon-info-parts {:weapon-name 9 :weapon-damage 10 :weapon-location 12 :weapon-type 11})

(defn line-victim-info [parts]
  (get-parts-by-name parts victim-info-parts))

(defn line-attacker-info [parts]
  (get-parts-by-name parts attacker-info-parts))

(defn line-weapon-info [parts]
  (get-parts-by-name parts weapon-info-parts))

(defn line-data [line]
  (let [data-parts (string/split line #";")
        type (first data-parts)
        victim (line-victim-info data-parts)
        attacker (line-attacker-info data-parts)
        weapon (line-weapon-info data-parts)]
    (merge {:type type} victim attacker weapon)))

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
