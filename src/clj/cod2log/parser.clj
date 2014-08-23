(ns cod2log.parser
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as string]))

;; types
;; J - join
;; Q - quit
;; K - kill
;; D - damage
;; W - winning team
;; L - losing team
;; Weapon - weapon pickup
;; say - all chat
;; sayteam - team chat
;; ExitLevel - unload
;; ShutdownGame: - round stop
;; ------ - empty lines
(defn line-type [parts]
  (first parts))

(defn line-victim-info [parts]
  (let [team (get parts 3)
        name (get parts 4)]
    {:victim-team team :victim-name name}))

(defn line-attacker-info [parts]
  (let [team (get parts 7)
        name (get parts 8)]
    {:attacker-team team :attacker-name name}))

(defn line-weapon-info [parts]
  (let [code (get parts 9)
        damage (get parts 10)
        type (get parts 11)
        location (get parts 12)]
    {:weapon-name code :weapon-damage damage :weapon-location location :weapon-type type}))

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

(defn parse-file [filename]
  (with-open [rdr (io/reader filename)]
    (doseq [line (line-seq rdr)]
      (pprint (line-time-and-data line)))))
