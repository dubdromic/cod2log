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
(defn line-type [line]
  :temp)

(defn line-time [line]
  (first (string/split line #"\s")))

(defn line-metadata [line]
  (let [time (line-time line)
        type (line-type line)]
    {:time time :type type}))

(defn parse-file [filename]
  (with-open [rdr (io/reader filename)]
    (doseq [line (line-seq rdr)]
      (pprint (line-metadata line)))))
