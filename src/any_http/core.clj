(ns any-http.core
  (:refer-clojure :exclude [get])
  (:require
   [clojure.string :as str]))


(defprotocol HTTPClient

  (get
    [this url]
    [this url options])

  (post
    [this url]
    [this url options])

  (put
    [this url]
    [this url options])

  (patch
    [this url]
    [this url options])

  (delete
    [this url]
    [this url options])

  (options
    [this url]
    [this url options])

  (request
    [this url]
    [this url options])

  (get-async
    [this url callback]
    [this url options callback])

  (post-async
    [this url callback]
    [this url options callback])

  (put-async
    [this url callback]
    [this url options callback])

  (patch-async
    [this url callback]
    [this url options callback])

  (delete-async
    [this url callback]
    [this url options callback])

  (options-async
    [this url callback]
    [this url options callback])

  (request-async
    [this url callback]
    [this url options callback]))


(defrecord Response
    [status
     body
     headers])


(defn make-response [status body headers]
  (new Response status body headers))


(defn json? [response]
  (some->> response :content-type (re-find #"(?i)application/json")))


(defn etag [response]

  )
