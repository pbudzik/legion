(ns legion.services
  (:use [legion.utils]
        [legion.handler]
        [legion.logging]
        [legion.server]))

(defn- parse-uri [^String uri] (first (split uri "\\?")))

(defn- tokenize [^String s]
  (let [t (seq (drop 1 (split s "/")))]
    (if (nil? t) '() t)))

(defstruct request :method :uri )

(defn- match-tokens [^String t1 ^String t2]
  (if (= t1 t2) {} (if (starts-with t2 ":") {(keyword (tail t2)) t1} nil)))

(defn- match-uri [^String u1 ^String u2]
  (let [t1 (tokenize u1)
        t2 (tokenize u2)]
    (if (= (count t1) (count t2))
      (let [pairs (partition 2 (interleave t1 t2))
            results (map #(match-tokens (first %) (last %)) pairs)]
        (if (some nil? results) nil (merge (reduce merge results) {})))
      nil)))

(defn match-rq [rq pattern]
  (if (= (:method rq)
        (:method pattern))
    {:pattern pattern :result (match-uri (:uri rq) (:uri pattern))} nil))

(defn find-handler [input services]
  (let [rq (struct request (:method input) (parse-uri (:uri input)))
        f1 (map #(match-rq rq %) (keys services))
        f2 (first (filter #(not (nil? (:result %))) f1))
        handler (if (empty? f2) H_404 (services (:pattern f2)))]
    (if (= handler H_404)
      handler
      (fn [input] (handler (assoc input :ext (:result f2)))))))

(defn- services-handler [services]
  (fn [rq]
    (let [handler ((find-handler rq services) rq)]
      (debug "handler: " handler)
      handler)))

(defn start [port services]
  (server-start port (services-handler services)))

(defn stop [server]
  (server-stop server))

(defmacro add-handler [method uri handler] `{(struct request ~method ~uri) ~handler})
(defmacro GET [uri handler] `(add-handler :get ~uri ~handler))
(defmacro POST [uri handler] `(add-handler :post ~uri ~handler))
(defmacro PUT [uri handler] `(add-handler :put ~uri ~handler))
(defmacro DELETE [uri handler] `(add-handler :delete ~uri ~handler))
(defmacro defservices [name & body] `(def ~name (merge ~@body)))
(defmacro gen-services [name mapping] `{})

