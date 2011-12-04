(ns legion.core
  (:use [legion.services]
        [legion.cluster]))

(defn cluster-start
  "Start services at given port and join a cluster"
  [cluster port services]
  {:server (start port services) :channel (connect SERVER cluster port)})

(defn cluster-stop
  "Stop services and diconnect from a cluster"
  [node]
  (stop (:server node))
  (disconnect (:channel node)))

(defn start-group [ports services & [cluster]]
  (map #(start % services) ports))

(defn stop-group [group]
  (doall (map #(stop %) group)))