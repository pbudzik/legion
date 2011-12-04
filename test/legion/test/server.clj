(ns legion.test.server
  (:use [clojure.test]
        [legion.server]
        [legion.client])
  (:require [clj-http.client :as client]))

;TODO :)

(deftest server
  (defn h [rq] (println rq) {:status 200 :content "foo" :headers {:foo 1}})
  (def s (server-start 8080 h))
  (println s)
  (try
    (do
      (println (client/get "http://localhost:8080/ss?a=1")))
    (finally (server-stop s))))

(run-tests)