(ns legion.test.cluster
  (:use [clojure.test]
        [legion.core]
        [legion.services]
        [legion.client]
        [legion.cluster]
        [cheshire.core]))

(deftest cluster
  (def node (clustered-start "bar" 9090 {}))
  (is (not (nil? (:channel node))))
  (clustered-stop (clustered-start "bar" 9191 {}))
  (clustered-stop node))

(run-tests)