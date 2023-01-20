(ns any-http.http-kit
  (:require
   [any-http.util :as util]
   [any-http.api :as api]
   [clojure.string :as str]
   [org.httpkit.client :as client]))


(def TAG ::http-kit)


(def overrides
  {:as :stream})


(defmacro perform [method url defaults options]
  `(let [request#
         (-> ~defaults
             (api/set-params ~TAG ~options)
             (merge ~overrides)
             (assoc :url ~url :method ~method))

         response#
         (client/request request#)]

     (let [response# @response#
           error#    (:error response#)
           status#   (:status response#)
           body#     (:body response#)
           headers#  (:headers response#)]

       (if error#
         (throw error#)
         (api/make-response status# body# headers#)))))


(alter-var-root #'api/h derive TAG ::api/client)


(defmethod api/set-param [TAG :headers]
  [_ params _ headers]
  (update params :headers merge (util/update-keys headers name)))


(defmethod api/set-param [TAG :timeout]
  [_ params _ timeout]
  (assoc params
         :timeout timeout
         :connect-timeout timeout
         ;; :idle-timeout timeout
         ))


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
   (new HTTPKitClient (api/set-params TAG defaults))))


(comment

  (def -c (client {:insecure? true}))

  (def -r (api/get -c "https://ya.ru"))

  )
