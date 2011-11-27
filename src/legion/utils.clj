(ns legion.utils)

(set! *warn-on-reflection* true)

(defn split [^String s separator] (.split s separator))

(defn starts-with [^String s c] (.startsWith s c))

(defn tail
  "Returns a string w/o the first character"
  [^String s] (.substring s 1 (count s)))