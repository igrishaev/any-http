(ns any-http.clj-http
  (:require
   [any-http.util :as util]
   [any-http.core :as core]
   [clojure.string :as str]
   [clj-http.client :as client]))


(def defaults-required
  {:as :stream
   :throw-exceptions true
   :coerce :always
   :socket-timeout 1000
   :connection-timeout 1000})


(defmacro perform [func url defaults options]
  `(let [resp#
         (~func ~url (merge ~defaults
                            ~options
                            defaults-required))]
     (-> resp#
         (update :headers util/update-keys util/header->kw)
         (util/as [{:keys [~'status ~'body ~'headers]}]
           (core/make-response ~'status ~'body ~'headers))
         #_
         (try
           (catch Throwable e#
             )))))


(deftype HTTPCljClient [defaults]

  core/HTTPClient

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

  (patch [this url]
    (perform client/patch url defaults nil))

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
   (new HTTPCljClient defaults)))


(comment

  (def -c (client))

  (core/get -c "https://ya.ru")

  )
