(ns legion.cluster
  (:use [legion.services]
        [legion.logging]
        [legion.utils])
  (:import [org.jgroups JChannel ReceiverAdapter Event Address PhysicalAddress]))

(System/setProperty "java.net.preferIPv4Stack" "true")

(defn- configure
  "All the trickery needed to configure a channel"
  [channel]
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

(def SERVER "S")

(def CLIENT "C")

(def instance-id (str (java.util.UUID/randomUUID)))

(defn- node-name [type port] (str instance-id ":" port ":" type))

(defn- server? [^String name] (ends-with name (str ":" SERVER)))

(defn- parse-port [^String name] (nth (split name ":") 1))

(defn- server [name ipa]
  (str (.getHostAddress (.getIpAddress ipa)) ":" (parse-port name)))

(defn- servers [channel view]
  (map #(server (str %) (.down channel (Event. (Event/GET_PHYSICAL_ADDRESS) %))) (filter #(server? (str %)) (.getMembers view))))

(defn- receiver [channel context]
  (proxy [org.jgroups.ReceiverAdapter] []
    (viewAccepted [view]
      (debug "view: " (.getMembers view))
      (if context
        (do
          (swap! context assoc :servers (servers channel view))
          (debug "context: " @context))))))

(defn connect
  ([type name port] (connect type name port (atom {})))
  ([type name port context]
    (let [channel (JChannel.)]
      (.setName channel (node-name type port))
      (debug channel)
      (configure channel)
      (.setReceiver channel (receiver channel context))
      (.connect channel name)
      channel)))

(defn disconnect [channel]
  (debug "disconnecting: " channel)
  (.close channel))

(defn- mask
  "Local IPs masking with 'localhost' to avoid getting non-accessbile IPs e.g. wlan interfaces.
  Useful in local mode"
  [^String server]
  (str "localhost:" (last (split server ":"))))

(defn cluster-select
  "Select node from a cluster by hashing key"
  [context cluster key]
  (if (nil? (:channel @context)) (swap! context assoc :channel (connect CLIENT cluster 0 context)))
  (debug "servers found: " (:servers @context))
  (let [server (select (:servers @context) (str key (System/nanoTime)))
        elected (if (= true (:mask @context)) (mask server) server)]
    (debug "server elected: " elected)
    elected))

