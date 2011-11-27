(defproject legion "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [
                  [org.clojure/clojure "1.3.0"]
                  [org.jboss.netty/netty "3.2.6.Final"]
                  [clj-http "0.1.3"]
                  [org.jgroups/jgroups "3.0.0.Final"]
                  [ch.qos.logback/logback-classic "0.9.28"]
                  [cheshire "2.0.2"]
                  ]
  :jvm-opts ["-server" "-XX:+UseConcMarkSweepGC"]
  :repositories {"jboss" "https://repository.jboss.org/nexus/content/repositories/releases/"}
  )

