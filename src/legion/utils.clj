(ns legion.utils)

(set! *warn-on-reflection* true)

(defn split [^String s separator] (seq (.split s separator)))

(defn starts-with [^String s c] (.startsWith s c))

(defn ends-with [^String s c] (.endsWith s c))

(defn tail
  "Returns a string w/o the first character"
  [^String s] (.substring s 1 (count s)))

(defn normalize-url [^String url]
  (if (.endsWith url "/") (.substring url 0 (- (.length url) 1)) url))

(defn select [coll key] (nth coll (mod (hash key) (count coll))))

(defmacro do-when [expr fun]
  `(when ~expr (~fun ~expr)))


