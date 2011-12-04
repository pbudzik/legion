(ns legion.server
  (:use [legion.utils]
        [legion.handler]
        [legion.logging]
        [cheshire.core])
  (:import [org.jboss.netty.bootstrap ServerBootstrap]
           [org.jboss.netty.channel.socket.nio NioServerSocketChannelFactory]
           [org.jboss.netty.channel ChannelPipelineFactory]
           [org.jboss.netty.channel ChannelPipeline]
           [java.util.concurrent Executors]
           [java.net InetSocketAddress]
           [org.jboss.netty.handler.codec.http HttpContentCompressor]
           [org.jboss.netty.handler.codec.http HttpRequestDecoder]
           [org.jboss.netty.handler.codec.http HttpResponseEncoder]
           [org.jboss.netty.channel Channels]
           [org.jboss.netty.channel SimpleChannelUpstreamHandler]
           [org.jboss.netty.handler.codec.http DefaultHttpResponse]
           [org.jboss.netty.handler.codec.http HttpVersion]
           [org.jboss.netty.handler.codec.http HttpResponseStatus]
           [org.jboss.netty.handler.codec.http.HttpHeaders.Names]
           [org.jboss.netty.buffer ChannelBuffers]
           [org.jboss.netty.util CharsetUtil]
           [org.jboss.netty.channel ChannelFutureListener]
           [org.jboss.netty.handler.codec.http QueryStringDecoder]
           )
  (:require [clj-http.client :as client]))

(defn- pipeline-factory [^SimpleChannelUpstreamHandler handler]
  (proxy [ChannelPipelineFactory] []
    (getPipeline []
      (doto (Channels/pipeline)
        (.addLast "decoder" (HttpRequestDecoder.))
        (.addLast "encoder" (HttpResponseEncoder.))
        (.addLast "deflater" (HttpContentCompressor.))
        (.addLast "handler" handler)))))

(defn- keywordify [m] (case m "GET" :get "POST" :post "DELETE" :delete "PUT" :put "HEAD" :head :get ))

(defn- process [rq handler]
  (handler {:method (keywordify (.getName (.getMethod rq)))
            :uri (.getUri rq)
            :headers (.getHeaders rq)
            :params (.getParameters (QueryStringDecoder. (.getUri rq)))
            }))

(defn- rs-status [^Integer code]
  (case code
    200 (HttpResponseStatus/OK)
    404 (HttpResponseStatus/NOT_FOUND)
    ;TODO ...
    (HttpResponseStatus/OK)))

(defn- request-handler [handler]
  (proxy [SimpleChannelUpstreamHandler] []
    (messageReceived [ctx e]
      (let [rq (.getMessage e)
            method (.getMethod rq)
            channel (.getChannel e)
            handler-rs (process rq handler)
            rs (DefaultHttpResponse. (HttpVersion/HTTP_1_1) (rs-status (:status handler-rs)))]
        (.setContent rs (ChannelBuffers/copiedBuffer (:content handler-rs) (CharsetUtil/UTF_8)))
        (.setHeader rs "Content-type" (or-else (:content-type handler-rs) CONTENT_TYPE_TEXT_PLAIN))
        (.addListener (.write channel rs) (ChannelFutureListener/CLOSE))))

    (exceptionCaught [ctx e]
      (error "http-server error: " e)
      (.close (.getChannel e)))))

(defn server-start [port handler]
  (let [factory (pipeline-factory (request-handler handler))
        server {:bootstrap (doto (ServerBootstrap. (NioServerSocketChannelFactory. (Executors/newCachedThreadPool) (Executors/newCachedThreadPool)))
                             (.setPipelineFactory factory)
                             (.bind (InetSocketAddress. port)))
                :started-at (System/currentTimeMillis)}]
    (debug "server at " port " started")
    server))

(defn server-stop [server]
  (.releaseExternalResources (:bootstrap server))
  (debug "server " server " stopped"))
