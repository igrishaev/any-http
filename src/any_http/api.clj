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


(defn ok? [response]
  (some-> response
          :status
          (util/as [status]
            (<= 200 status 299))))


(def h
  (-> (make-hierarchy)
      (derive :url           ::param)
      (derive :method        ::param)
      (derive :body          ::param)
      (derive :query-params  ::param)
      (derive :form-params   ::param)
      (derive :headers       ::param)
      (derive :multipart     ::param)
      (derive :basic-auth    ::param)
      (derive :insecure?     ::param)
      (derive :oauth-token   ::param)))


(defmulti set-param
  (fn [tag params param value]
    [tag param])
  :hierarchy #'h)


(defmethod set-param :default
  [_ params param value]
  params)


(def TAG ::client)


(defmethod set-param [TAG ::param]
  [_ params param value]
  (assoc params param value))


(defmethod set-param [TAG :headers]
  [_ params _ headers]
  (update params :headers merge headers))


(defmethod set-param [TAG :query-params]
  [_ params _ query-params]
  (update params :query-params merge query-params))


(defmethod set-param [TAG :form-params]
  [_ params _ form-params]
  (update params :form-params merge form-params))


#_
(defmethod set-param [TAG :oauth-token]
  [_ params _ oauth-token]
  (update params :oauth-token oauth-token))


(defn set-params

  ([tag params]
   (set-params nil tag params))

  ([acc tag params]
   (reduce-kv
    (fn [acc k v]
      (set-param tag acc k v))
    acc
    params)))

;; socket-timeout
;; connection-timeout
;; :max-redirects
;; :follow-redirects
;; :keepalive

;; components
