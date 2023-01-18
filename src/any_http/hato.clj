(ns any-http.hato
  (:require
   [any-http.util :as util]
   [any-http.api :as api]
   [clojure.string :as str]
   [hato.client :as client]))


(def common-params
  (conj api/common-params :http-client))


(def overrides
  {:as :stream
   :throw-exceptions false})


(defmacro perform [method url defaults options]
  `(let [request#
         (-> ~defaults
             (merge ~options)
             (select-keys common-params)
             (merge ~overrides)
             (update :headers util/update-keys name)
             (assoc :url ~url
                    :method ~method))

         response#
         (client/request request#)]

     (-> response#
         (update :headers util/update-keys keyword)
         (util/as [{:keys [~'status ~'body ~'headers]}]
           (api/make-response ~'status ~'body ~'headers)))))


(deftype HatoClient [defaults]

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
   (new HatoClient defaults)))


(defn component

  ([]
   (component nil))

  ([defaults]
   (with-meta (client defaults)
     {'com.stuartsierra.component/start
      (fn [this]
        (assoc-in this
                  [:defaults :http-client]
                  (client/build-http-client nil)))
      'com.stuartsierra.component/stop
      (fn [this]
        this
        #_
        (update-in this
                   [:defaults :http-client]
                   (fn [http-client]
                     (when http-client
                       ))))})))


(comment

  (def -c (client))

  (api/get -c "https://ya.ru")

  )
