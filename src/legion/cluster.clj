(ns legion.cluster
  (:use [legion.logging]
        [legion.services]))

(System/setProperty "java.net.preferIPv4Stack" "true")

(defn- configure [channel]
  (let [stack (org.jgroups.stack.ProtocolStack.)]
    (.addProtocol stack
      (doto (org.jgroups.protocols.UDP.)
        (.setValue "ucast_recv_buf_size" (java.lang.Integer. 130000))
        (.setValue "ucast_send_buf_size" (java.lang.Integer. 130000))
        (.setValue "mcast_send_buf_size" (java.lang.Integer. 130000))
        (.setValue "mcast_recv_buf_size" (java.lang.Integer. 130000))))
    (.addProtocol stack (org.jgroups.protocols.PING.))
    (.addProtocol stack (org.jgroups.protocols.MERGE2.))
    (.addProtocol stack (org.jgroups.protocols.FD_SOCK.))
    (.addProtocol stack
      (doto (org.jgroups.protocols.FD_ALL.)
        (.setValue "timeout" 12000)
        (.setValue "interval" 3000)))
    (.addProtocol stack (org.jgroups.protocols.VERIFY_SUSPECT.))
    (.addProtocol stack (org.jgroups.protocols.BARRIER.))
    (.addProtocol stack (org.jgroups.protocols.pbcast.NAKACK.))
    (.addProtocol stack (org.jgroups.protocols.UNICAST2.))
    (.addProtocol stack (org.jgroups.protocols.pbcast.STABLE.))
    (.addProtocol stack (org.jgroups.protocols.pbcast.GMS.))
    (.addProtocol stack (org.jgroups.protocols.UFC.))
    (.addProtocol stack (org.jgroups.protocols.MFC.))
    (.addProtocol stack (org.jgroups.protocols.FRAG2.))
    (.setProtocolStack channel stack)
    (.init stack)))

(def instance-id (str (java.util.UUID/randomUUID)))

(defn- node-name [port] (str instance-id ":" port ":S"))

(defn connect [name port]
  (let [channel (org.jgroups.JChannel.)]
    (.setName channel (node-name port))
    (debug channel)
    (configure channel)
    (.connect channel name)
    channel))

(defn disconnect [channel]
  (.close channel))

(defn clustered-start [cluster port services]
  {:server (start port services) :channel (connect cluster port)})

(defn clustered-stop [node]
  (stop (:server node))
  (disconnect (:channel node)))



