(ns any-http.clj-http-lite
  (:require
   [any-http.util :as util]
   [any-http.api :as api]
   [clojure.string :as str]
   [clj-http.lite.client :as client]))


(def defaults-required
  {:as :stream
   :content-type nil
   :throw-exceptions true
   :coerce :always
   :socket-timeout 1000
   :connection-timeout 1000})


(defmacro perform [func url defaults options]
  `(let [resp#
         (~func ~url (-> ~defaults
                         (merge
                          ~options
                          defaults-required)
                         (update :headers util/update-keys name)))]

     (-> resp#
         (update :headers util/update-keys keyword)
         (util/as [{:keys [~'status ~'body ~'headers]}]
           (api/make-response ~'status ~'body ~'headers))
         #_
         (try
           (catch Throwable e#
             )))))


(deftype CljHTTPLiteClient [defaults]

  api/HTTPClient

  (get [this url]
    (perform client/get url defaults nil))

  (get [this url options]
    (perform client/get url defaults options))

  (post [this url]
    (perform client/post url defaults nil))

  (post [this url options]
    (perform client/post url defaults options))

  (put [this url]
    (perform client/put url defaults nil))

  (put [this url options]
    (perform client/put url defaults options))

  #_
  (patch [this url]
    (perform client/patch url defaults nil))

  #_
  (patch [this url options]
    (perform client/patch url defaults options))

  (delete [this url]
    (perform client/delete url defaults nil))

  (delete [this url options]
    (perform client/delete url defaults options)))


(defn client
  ([]
   (client nil))
  ([defaults]
   (new CljHTTPLiteClient defaults)))


(comment

  (def -c (client))

  (api/get -c "https://ya.ru")

  )
