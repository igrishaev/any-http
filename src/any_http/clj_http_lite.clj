(ns any-http.clj-http-lite
  (:require
   [any-http.util :as util]
   [any-http.api :as api]
   [clojure.string :as str]
   [clj-http.lite.client :as client]))


(def TAG ::http-kit)


(def overrides
  {:as :stream
   :throw-exceptions false})


(defmacro perform [func url defaults options]
  `(let [request#
         (-> ~defaults
             (api/set-params ~TAG ~options)
             (merge ~overrides))

         response#
         (~func ~url request#)]

     (-> response#
         (update :headers util/update-keys keyword)
         (util/as [{:keys [~'status ~'body ~'headers]}]
           (api/make-response ~'status ~'body ~'headers)))))


(alter-var-root #'api/h derive TAG ::api/client)


(defmethod api/set-param [TAG :timeout]
  [_ params _ timeout]
  (assoc params
         :socket-timeout timeout
         :conn-timeout timeout))


(defmethod api/set-param [TAG :headers]
  [_ params _ headers]
  (update params :headers merge (util/update-keys headers name)))


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

  (def -c (client {:insecure? true}))

  (def -r (api/get -c "https://ya.ru"))

  )
