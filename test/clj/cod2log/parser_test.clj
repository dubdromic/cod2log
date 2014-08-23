(ns cod2log.parser-test
  (:require [clojure.test :refer :all]
            [cod2log.parser :refer :all]
            [clojure.string :as string])
  (:import (java.io BufferedReader StringReader)))

(def lines
  ["21633:00 K;0;1;allies;bob loblaw;0;3;axis;Gilbert Justice;kar98k_sniper_mp;135;MOD_RIFLE_BULLET;torso_lower"
   "21633:08 D;0;1;allies;bob loblaw;0;0;axis;your hopes and dreams;frag_grenade_german_mp;60;MOD_GRENADE_SPLASH;none"])

(def lines-without-time
  (map #(second (string/split % #"\s" 2)) lines))

(deftest line-victim-info-test
  (testing "line-victim-info returns hash with nil values if line isn't valid"
    (is (= {:victim-team nil :victim-name nil} (line-victim-info ["J" "0" "1"]))))
  (testing "line-victim-info returns correct victim info"
    (is (= {:victim-team "allies" :victim-name "bob loblaw"} (line-victim-info ["K" "0" "1" "allies" "bob loblaw"])))))

(deftest line-attacker-info-test
  (testing "line-attacker-info returns hash with nil values if line isn't valid"
    (is (= {:attacker-team nil :attacker-name nil} (line-attacker-info ["K" "0" "1" "Simo Hayha"]))))
  (testing "line-attacker-info returns correct attacker info"
    (is (= {:attacker-team "axis" :attacker-name "Simo Hayha"} (line-attacker-info ["K" "0" "1" "allies" "bob loblaw" "0" "0" "axis" "Simo Hayha"])))))

(deftest line-weapon-info-test
  (testing "line-weapon-info returns correct weapon info"
    (is (= {:weapon-name "enfield_mp" :weapon-damage "100" :weapon-location "torso_lower" :weapon-type "MOD_RIFLE_BULLET"}
           (line-weapon-info ["K" "0" "1" "allies" "bob loblaw" "0" "0" "axis" "Simo Hayha" "enfield_mp" "100" "MOD_RIFLE_BULLET" "torso_lower"])))))

(deftest line-data-test
  (testing "line-data returns map of line data without time"
    (is (= {:weapon-damage "135",
            :weapon-location "torso_lower",
            :weapon-name "kar98k_sniper_mp",
            :weapon-type "MOD_RIFLE_BULLET",
            :attacker-name "Gilbert Justice",
            :attacker-team "axis",
            :victim-name "bob loblaw",
            :victim-team "allies",
            :type "K"}
           (line-data (first lines-without-time))))))

(deftest line-time-and-data-test
  (testing "line-time-and-data-test returns line-data with time"
    (is (= {:weapon-damage "60",
            :weapon-location "none",
            :attacker-name "your hopes and dreams",
            :attacker-team "axis",
            :victim-name "bob loblaw",
            :victim-team "allies",
            :weapon-name "frag_grenade_german_mp",
            :type "D",
            :weapon-type "MOD_GRENADE_SPLASH",
            :time "21633:08"} 
           (line-time-and-data (second lines))))))

(deftest parse-lines-test
  (testing "it parses lines passed in from a BufferedReader"
    (let [formatted-lines (BufferedReader. (StringReader. (string/join "\n" lines)))]
      (is (= [{:weapon-damage "135",
               :weapon-location "torso_lower",
               :attacker-name "Gilbert Justice",
               :attacker-team "axis",
               :victim-name "bob loblaw",
               :victim-team "allies",
               :weapon-name "kar98k_sniper_mp",
               :type "K",
               :weapon-type "MOD_RIFLE_BULLET",
               :time "21633:00"}
              {:weapon-damage "60",
               :weapon-location "none",
               :attacker-name "your hopes and dreams",
               :attacker-team "axis",
               :victim-name "bob loblaw",
               :victim-team "allies",
               :weapon-name "frag_grenade_german_mp",
               :type "D",
               :weapon-type "MOD_GRENADE_SPLASH",
               :time "21633:08"}]
             (parse-lines formatted-lines))))))
