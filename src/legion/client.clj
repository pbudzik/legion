(ns legion.client
  (:use [legion.utils]
        [legion.logging]
        [cheshire.core])
  (:require [clj-http.client :as client]))

(defn build-url [options args]
  (cond
    (:url options) (str (:url options) "/" (apply str (interpose "/" args)))
    (:servers options) "foo"
    (:cluster options) "foo"
    ))

(defn resolve-method [^String name options]
  (if (:method options) (:method options)
    (cond
      (starts-with name "get-") :get (starts-with name "update-") :put (starts-with name "delete-") :delete (starts-with name "add-") :post :else :get )))

(defmacro defclient [name options args]
  `(defn ~name [~@args & c#]
     (debug "content: " c#)
     (let [url# (build-url ~options ~args)
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

(defclient get-bar {:url "http://wp.pl:80"} [id x])
(get-bar 1 2)

;(defclient get-baz {:servers "wp.pl:80,onet.pl:80" :uri "/foo"} [id x])
;(get-baz 1 2)

;(defclient get-baz {:cluster "foo" :uri "/foo"} [id x])
