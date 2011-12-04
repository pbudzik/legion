--Legion--
==========

Minimalistic REST services framework for Clojure
------------------------------------------------

### Core concepts ###

* succinct but expressible
* convention over configuration
* server/client unified
* scalable
* clustered
* failover
* leightweight

```clj
(defproject my-project "1.0.0"
  :dependencies [[org.clojure/clojure "1.3.0"]
				 [legion "1.0.0-SNAPSHOT"]])
```

### Starting services ###
```clj
(defn my-handler [rq] (response 200 {:firstname "John" :lastname "Doe" :email "John.Doe@foo.com"}))

(defservices my-services
  (GET "/user/:id" my-handler))
```
### Defining service consumer ###
```clj
(defclient get-user {:cluster "my-cluster" :uri "/user"} [id])
```
### Consuming services ###
```clj
(let [services (cluster-start "my-cluster" 8080 my-services)]
  (try
    (println (get-user 19811))
    (finally
      (cluster-stop services)
      (get-user-destroy))))
```
### Result ###
```clj
{:status 200, :headers {content-type text/plain; charset=UTF-8}, :body {"email":"John.Doe@foo.com","firstname":"John","lastname":"Doe"}}
```