(ns example.example
  (:use [clojure.test]
        [legion.core]
        [legion.services]
        [legion.client]
        [legion.cluster]
        [legion.handler]
        [legion.logging]
        [cheshire.core]))

(defn my-handler [rq] (response 200 {:firstname "John" :lastname "Doe" :email "John.Doe@foo.com"}))

(defservices my-services
  (GET "/user/:id" my-handler))

(defclient get-user {:cluster "my-cluster" :uri "/user" :mask true} [id])

(let [services (cluster-start "my-cluster" 8080 my-services)]
  (try
    (println (get-user 19811))
    (finally
      (cluster-stop services)
      (get-user-destroy))))