(ns any-http.http-kit
  (:require
   [any-http.util :as util]
   [any-http.api :as api]
   [clojure.string :as str]
   [org.httpkit.client :as client]))


(def defaults-required
  {:as :stream
   :content-type nil
   :timeout 2000})


(defmacro perform [method url defaults options]
  `(let [resp#
         (client/request (-> {:url ~url
                              :method ~method}
                             (merge
                              ~defaults
                              ~options
                              defaults-required)
                             (update :headers util/update-keys name)))]

     (-> @resp#
         (util/as [{:keys [~'status ~'body ~'headers]}]
           (api/make-response ~'status ~'body ~'headers))
         #_
         (try
           (catch Throwable e#
             )))))

(deftype HTTPKitClient [defaults]

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
   (new HTTPKitClient defaults)))


(comment

  (def -c (client))

  (api/get -c "https://ya.ru")

  )
