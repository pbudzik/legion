(ns legion.handler
  (:use [cheshire.core]))

(def CONTENT_TYPE_TEXT_PLAIN "text/plain; charset=UTF-8")

(def CONTENT_TYPE_APP_JSON "application/json; charset=UTF-8")

(defn H_404 "Dummy 404 HTTP handler" [rq] {:status 404 :content "{}"})

(defn H_200 "Dummy 200 HTTP handler" [rq] {:status 200 :content-type CONTENT_TYPE_APP_JSON :content (generate-string {:a 1 :b "x"})})

(defmacro error-response [code message] `{:status 502 :content (generate-string {:error ~code :message ~message})})
(defmacro response [status value] `{:status ~status :content (generate-string ~value)})

