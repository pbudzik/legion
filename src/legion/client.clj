(ns legion.client
  (:use [legion.utils]
        [legion.logging]
        [legion.cluster]
        [cheshire.core])
  (:require [clj-http.client :as client]))

(defn parse-servers [servers]
  (map #(str "http://" %) (split servers ",")))

(defn build-url [context options args]
  (cond
    (:url options) (str (normalize-url (:url options)) "/" (apply str (interpose "/" args)))
    (:servers options) (select (parse-servers (:servers options) args))
    (:cluster options) (str "http://" (cluster-select context (:cluster options) args) (:uri options) "/" (apply str (interpose "/" args)))
    ))

(defn resolve-method [^String name options]
  (if (:method options) (:method options)
    (cond
      (starts-with name "get-") :get (starts-with name "update-") :put (starts-with name "delete-") :delete (starts-with name "add-") :post :else :get )))

(defmacro defclient [name options args]
  (let [destroyer (symbol (str name "-destroy"))]
    `(do
       (def context# (atom ~options))
       (defn ~destroyer [] (do-when (:channel @context#) disconnect))
       (defn ~name [~@args & c#]
         (debug "content: " c#)
         (let [url# (build-url context# ~options ~args)
               json# (generate-string (first c#))]
           (debug "url: " url# ", json: " json#)
           (case (resolve-method (str ~name) ~options)
             :get (client/get url#)
             :post (client/post url#)
             :put (client/post url#)
             :delete (client/post url#)
             :head (client/head url#))
           )))))
;-----

(macroexpand '(defclient get-baz {:cluster "foo" :uri "/foo"} [id x]))

;(defclient get-baz {:cluster "foo" :uri "/foo"} [id x])

;(get-baz 1 2)

;(defclient get-bar {:url "http://wp.pl:80"} [id x])

;(get-bar 1 2)

;(defclient get-baz {:servers "wp.pl:80,onet.pl:80" :uri "/foo"} [id x])
;(get-baz 1 2)


