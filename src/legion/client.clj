(ns legion.client
  (:use [legion.utils]
        [legion.logging]
        [legion.cluster]
        [cheshire.core])
  (:require [clj-http.client :as client]))

(defn parse-servers [servers]
  (map #(str "http://" %) (split servers ",")))

(defn round-robin [context urls]
  (let [index (:last @context)
        url (nth urls index)]
    (swap! context assoc :last (mod (+ 1 index) (count urls)))
    url))

(defn build-url [context options args]
  (cond
    (:url options) (str (normalize-uri (:url options)) "/" (apply str (interpose "/" args)))
    (:servers options) (round-robin (parse-servers (:servers options)))
    (:cluster options) (select-url (:cluster options))
    ))

(defn resolve-method [^String name options]
  (if (:method options) (:method options)
    (cond
      (starts-with name "get-") :get (starts-with name "update-") :put (starts-with name "delete-") :delete (starts-with name "add-") :post :else :get )))

(defmacro defclient [name options args]
  `(defn ~name [~@args & c#]
     (def context# (atom {}))
     (debug "content: " c#)
     (let [url# (build-url context# ~options ~args)
           json# (generate-string (first c#))]
       (debug "url: " url# ", json: " json#)
       (case (resolve-method (str ~name) ~options)
         :get (client/get url#)
         :post (client/post url#)
         :put (client/post url#)
         :delete (client/post url#)
         :head (client/head url#)
         )
       )))

;-----

(def ctx (atom {:last 0}))

(println (round-robin ctx (parse-servers "localhost:8080,localhost:9090")))
(println (round-robin ctx (parse-servers "localhost:8080,localhost:9090")))
(println (round-robin ctx (parse-servers "localhost:8080,localhost:9090")))
(println (round-robin ctx (parse-servers "localhost:8080,localhost:9090")))
(println (round-robin ctx (parse-servers "localhost:8080,localhost:9090")))

;(defclient get-bar {:url "http://wp.pl:80"} [id x])

;(get-bar 1 2)

;(defclient get-baz {:servers "wp.pl:80,onet.pl:80" :uri "/foo"} [id x])
;(get-baz 1 2)

;(defclient get-baz {:cluster "foo" :uri "/foo"} [id x])

