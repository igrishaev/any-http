(ns any-http.api
  (:refer-clojure :exclude [get])
  (:require
   [any-http.util :as util]
   [clojure.string :as str]))


(defprotocol HTTPClient

  ;; sync

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

  ;; async

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


(defn header [response kw-header]
  (some-> response
          :headers
          (clojure.core/get kw-header)))


(defn content-type [response]
  (some-> response
          (header :content-type)
          (str/split #"\s*;\s*" 2)
          first
          str/lower-case))


(defn charset [response]
  (when-let [ct
             (some-> response
                     (header :content-type))]
    (second (re-find #"charset\s*=\s*(.+)" ct))))


(defn content-length [response]
  (some-> response
          (header :content-length)
          (util/parse-long)))


(defn json? [response]
  (-> response
      content-type
      (= "application/json")))


(defn etag [response]
  (header response :etag))
