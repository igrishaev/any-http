(ns any-http.aleph
  (:require
   [any-http.util :as util]
   [any-http.api :as api]
   [clojure.string :as str]
   [aleph.http :as client]))


(def defaults-required
  {:as :stream
   :timeout 2000})


(defmacro perform [method url defaults options]
  `(let [resp#
         (client/request (merge {:url ~url
                                 :method ~method}
                                ~defaults
                                ~options
                                defaults-required))]
     (-> @resp#
         (update :headers util/update-keys keyword)
         (util/as [{:keys [~'status ~'body ~'headers]}]
           (api/make-response ~'status ~'body ~'headers))
         #_
         (try
           (catch Throwable e#
             )))))

(deftype AlephClient [defaults]

  api/HTTPClient

  (get [this url]
    (perform :get url defaults nil))

  (get [this url options]
    (perform :get url defaults options))

  (post [this url]
    (perform :post url defaults nil))

  (post [this url options]
    (perform :post url defaults options))

  (put [this url]
    (perform :put url defaults nil))

  (put [this url options]
    (perform :put url defaults options))

  (patch [this url]
    (perform :patch url defaults nil))

  (patch [this url options]
    (perform :patch url defaults options))

  (delete [this url]
    (perform :delete url defaults nil))

  (delete [this url options]
    (perform :delete url defaults options)))


(defn client
  ([]
   (client nil))
  ([defaults]
   (new AlephClient defaults)))


(comment

  (def -c (client))

  (api/get -c "https://ya.ru")

  )
