(ns legion.logging)

(def logger (org.slf4j.LoggerFactory/getLogger "legion"))

(defn trace [& msg] (if (.isTraceEnabled logger) (.trace logger (apply str msg))))

(defn debug [& msg] (if (.isDebugEnabled logger) (.debug logger (apply str msg))))

(defn info [& msg] (if (.isInfoEnabled logger) (.info logger (apply str msg))))

(defn warn [& msg] (if (.isWarnEnabled logger) (.warn logger (apply str msg))))

(defn error [& msg] (if (.isErrorEnabled logger) (.error logger (apply str msg))))

