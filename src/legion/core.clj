(ns legion.core
  (:use [legion.services]))

(defn start-group [ports services & [{:keys [cluster] :or {cluster false}}]]
  (map #(start % services) ports))

(defn stop-group [group]
  (doall (map #(stop %) group)))


