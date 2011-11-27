(ns legion.test.core
  (:use [clojure.test]
        [legion.core]
        [legion.services]
        [legion.client]
        [legion.handler]
        [legion.logging]
        [cheshire.core])
  (:require [clj-http.client :as client]))

(deftest matching
  (defservices test-services
    (GET "/" H_200)
    (GET "/foo/:id" H_200)
    (POST "/foo/:id" H_200))
  (is (= {:id "2"} (:result (match-rq {:method :get :uri "/foo/2"} {:method :get :uri "/foo/:id"}))))
  (is (= {:id "a" :foo "x"} (:result (match-rq {:method :get :uri "/foo/a/x"} {:method :get :uri "/foo/:id/:foo"}))))
  (is (= nil (:result (match-rq {:method :get :uri "/"} {:method :get :uri "/foo/:id/:foo"}))))
  (is (= nil (:result (match-rq {:method :get :uri "/d"} {:method :get :uri "/"}))))
  (is (= {} (:result (match-rq {:method :get :uri "/"} {:method :get :uri "/"}))))
  (is (= nil (:result (match-rq {:method :get :uri "/d/1"} {:method :get :uri "/a/:i"}))))
  (is (= nil (:result (match-rq {:method :post :uri "/"} {:method :get :uri "/"})))))

(deftest routing
  (defn h1 [input] 1)
  (defn h2 [input] 2)
  (defn h3 [input] 3)
  (defservices test-services
    (GET "/" h1)
    (GET "/foo/:id" h2)
    (POST "/foo/:id" h3))
  (is (= H_404 (find-handler {:method :get :uri "/bar"} test-services)))
  (is (= 2 ((find-handler {:method :get :uri "/foo/1"} test-services) {})))
  (is (= 1 ((find-handler {:method :get :uri "/"} test-services) {})))
  (is (= 1 ((find-handler {:method :get :uri "/?a=1"} test-services) {})))
  (is (= 3 ((find-handler {:method :post :uri "/foo/3"} test-services) {}))))

(deftest server
  (defn baz [rq] {:status 200 :content (str rq)})
  (defservices s1
    (GET "/" baz)
    (GET "/foo/:id" baz)
    (POST "/foo/:id" baz))
  (defclient get-foo {:url "http://localhost:8080/foo"} [id])
  (let [services (start 8080 s1)]
    (try
      (is (= 200 (:status (get-foo 1))))
      (finally (stop services)))))

(deftest groups
  (def g (start-group [9051 9052 9053] {}))
  (try
    (is (= (count g) 3))
    (finally (stop-group g))))

(deftest logging
  (debug "debug")
  (info "info")
  (warn "warn")
  (error "error")
  (trace "trace"))

(run-tests)

