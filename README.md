Legion - Minimalistic REST services framework for Clojure
---------------------------------------------------------

### Core concepts ###

* succinct but expressible -> lots of macros used, less is more
* convention over configuration -> save time by typing less
* netty/http/json -> simple
* server/client unified -> both share the same conventions
* scalable -> not about one instance, but many
* clustered -> join a cluster and work w/o configuration
* failover -> many nodes
* leightweight -> not many dependencies, no external processes needed

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
By convention it is GET. It builds a dedicated consumer function to GET this
service with an argument "id".
It also creates a function to destroy it when done to disconnect from the cluster.
Note, it only needs to know the cluster name.

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