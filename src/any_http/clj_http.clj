(ns any-http.clj-http
  (:require
   [any-http.util :as util]
   [any-http.api :as api]
   [clojure.string :as str]
   [clj-http.client :as client]
   [clj-http.conn-mgr :as conn-mgr]))


(def TAG ::clj-http)


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
         (update :headers util/update-keys util/header->kw)
         (util/as [{:keys [~'status ~'body ~'headers]}]
           (api/make-response ~'status ~'body ~'headers)))))


(alter-var-root #'api/h derive TAG ::api/client)


(defmethod api/set-param [TAG :timeout]
  [_ params _ timeout]
  (assoc params
         :socket-timeout timeout
         :connection-timeout timeout))


(deftype CljHTTPClient [defaults]

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
   (new CljHTTPClient (api/set-params TAG defaults))))


(defn component

  ([]
   (component nil))

  ([defaults]
   (with-meta (client defaults)
     {'com.stuartsierra.component/start
      (fn [this]
        (assoc-in this
                  [:defaults :connection-manager]
                  (conn-mgr/make-reusable-conn-manager nil)))
      'com.stuartsierra.component/stop
      (fn [this]
        (update-in this
                   [:defaults :connection-manager]
                   (fn [mgr]
                     (when mgr
                       (conn-mgr/shutdown-manager mgr)))))})))


(comment

  (def -c (client))

  (def -r (api/get -c "https://ya.ru"))

  )
