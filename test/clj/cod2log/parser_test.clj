(ns cod2log.parser-test
  (:require [clojure.test :refer :all]
            [cod2log.parser :refer :all]))

(deftest line-metadata-test
  (testing "it returns the line time and type"
    (is (= {:time "10:0" :type :temp}
           (line-metadata "10:0 J;test")))))

(deftest line-type-test
  (testing "line-type returns :init"
    (is (= :temp
           (line-type 'test')))))

(deftest line-time-test
  (testing "it returns the time of a line"
    (is (= "10:0"
           (line-time "10:0 test")))
    (is (= "0:0"
           (line-time "0:0 test")))
    (is (= "2000:10"
           (line-time "2000:10 K;extra;data")))))
