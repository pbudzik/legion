(ns legion.handler
  (:use [cheshire.core]))

(defn H_404 [input] {:status 404 :content "{}"})

(defn H_200 [input] {:status 200 :content (generate-string {:a 1})})
