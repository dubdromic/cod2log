(ns cod2log.parser-test
  (:require [clojure.test :refer :all]
            [cod2log.parser :refer :all]
            [clojure.string :as string])
  (:import (java.io BufferedReader StringReader)))

(def lines
  ["21633:00 K;0;1;allies;bob loblaw;0;3;axis;Gilbert Justice;kar98k_sniper_mp;135;MOD_RIFLE_BULLET;torso_lower"
   "21633:08 D;0;1;allies;bob loblaw;0;0;axis;your hopes and dreams;frag_grenade_german_mp;60;MOD_GRENADE_SPLASH;none"
   "21630:59 InitGame: \\g_antilag\\1\\g_gametype\\tdm\\gamename\\Call of Duty 2\\mapname\\mp_toujane\\protocol\\118\\scr_friendlyfire\\1\\scr_killcam\\0\\shortversion\\1.3\\sv_allowAnonymous\\0\\sv_floodProtect\\1\\sv_hostname\\^1RT Creative CoD2\\sv_maxclients\\16\\sv_maxPing\\350\\sv_maxRate\\25000\\sv_minPing\\0\\sv_privateClients\\0\\sv_punkbuster\\0\\sv_pure\\1\\sv_voice\\1"
   "10:00 -------------------------------------------"])

(def lines-without-time
  (map #(second (string/split % #"\s" 2)) lines))

(def kill-event-line (first lines-without-time))
(def damage-event-line (second lines-without-time))
(def init-event-line (nth lines-without-time 2))
(def bs-event-line (nth lines-without-time 3))

(deftest line-data-test
  (testing "line-data returns map of line data based on event type"
    (is (= {:weapon-damage "135",
            :weapon-location "torso_lower",
            :weapon-name "kar98k_sniper_mp",
            :weapon-type "MOD_RIFLE_BULLET",
            :attacker-name "Gilbert Justice",
            :attacker-team "axis",
            :victim-name "bob loblaw",
            :victim-team "allies",
            :type "K"}
           (line-data kill-event-line)))
    (is (= {:weapon-type "MOD_GRENADE_SPLASH",
            :type "D",
            :weapon-name "frag_grenade_german_mp",
            :victim-team "allies",
            :victim-name "bob loblaw",
            :attacker-team "axis",
            :attacker-name "your hopes and dreams",
            :weapon-location "none",
            :weapon-damage "60"}
           (line-data damage-event-line)))
    (is (= {:map "mp_toujane" :type "InitGame"}
           (line-data init-event-line)))
    (is (= {:type nil}
           (line-data bs-event-line)))))

(deftest line-time-and-data-test
  (testing "line-time-and-data-test returns line-data with time"
    (is (= {:map "mp_toujane" :type "InitGame" :time "21630:59"}
           (line-time-and-data (nth lines 2))))
    (is (= {:type nil :time "10:00"}
           (line-time-and-data (nth lines 3))))))

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
               :time "21633:08"}
              {:map "mp_toujane" :type "InitGame" :time "21630:59"}
              {:type nil :time "10:00"}]
             (parse-lines formatted-lines))))))
