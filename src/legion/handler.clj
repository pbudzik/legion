(ns legion.handler
  (:use [cheshire.core]))

(defn H_404 "Dummy 404 HTTP handler" [rq] {:status 404 :content "{}"})

(defn H_200 "Dummy 200 HTTP handler" [rq] {:status 200 :content (generate-string {:a 1})})
