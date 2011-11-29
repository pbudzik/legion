(ns legion.utils)

(set! *warn-on-reflection* true)

(defn split [^String s separator] (seq (.split s separator)))

(defn starts-with [^String s c] (.startsWith s c))

(defn ends-with [^String s c] (.endsWith s c))

(defn tail
  "Returns a string w/o the first character"
  [^String s] (.substring s 1 (count s)))

(defn normalize-uri [^String uri]
  (if (.endsWith uri "/") (.substring uri 0 (- (.length uri) 1)) uri))

