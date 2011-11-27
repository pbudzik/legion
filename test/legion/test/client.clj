(ns legion.test.client
  (:use [clojure.test]
        [legion.core]
        [legion.server]
        [legion.client]
        [legion.handler]
        [cheshire.core])
  (:require [clj-http.client :as client]))

(deftest method
  (is (= :get (resolve-method "aadd-foo" {})))
  (is (= :post (resolve-method "add-foo" {})))
  (is (= :put (resolve-method "update-foo" {})))
  (is (= :get (resolve-method "get-foo" {})))
  (is (= :delete (resolve-method "get-foo" {:method :delete})))
  (is (= :delete (resolve-method "delete-foo" {})))
  (is (= :get (resolve-method "add-foo" {:method :get}))))

(deftest url
  (is (= "http://foo.com/1/2/3" (build-url {:url "http://foo.com"} [1 2 3])))

  )

(run-tests)
