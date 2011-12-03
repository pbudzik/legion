(ns legion.test.cluster
  (:use [clojure.test]
        [legion.core]
        [legion.services]
        [legion.client]
        [legion.cluster]
        [cheshire.core]))

(deftest cluster
  (def node (cluster-start "bar" 9090 {}))
  (is (not (nil? (:channel node))))
  (cluster-stop (cluster-start "bar" 9191 {}))
  (cluster-stop node))

(run-tests)

