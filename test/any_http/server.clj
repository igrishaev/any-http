(ns any-http.server
  (:require
   [clojure.pprint :as pprint]
   [clojure.string :as str]
   [ring.adapter.jetty :refer [run-jetty]]
   [ring.middleware.json :refer [wrap-json-response
                                 wrap-json-params]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.params :refer [wrap-params]]))


(def not-found
  {:status 404
   :body {:error "Page not found"}})


(defn make-app [method->path->response]
  (fn [request]
    (let [{:keys [params]} request
          {:keys [request-method uri]} request]

      (printf "HTTP %s %s %s"
              (-> request-method name str/upper-case)
              uri
              params)

      (let [response
            (get-in method->path->response
                    [request-method uri])]

        (cond
          (nil? response)
          not-found

          (map? response)
          response

          (fn? response)
          (response request)

          :else
          (throw (new Exception "Wrong response type")))))))


(defmacro with-http
  [[port method->path->response] & body]
  `(let [app# (-> ~method->path->response
                  make-app
                  wrap-keyword-params
                  wrap-json-params
                  wrap-params
                  wrap-json-response)

         server# (run-jetty app# {:port ~port :join? false})]
     (try
       ~@body
       (finally
         (.stop server#)))))


#_
(comment

  (require 'org.httpkit.client)

  (with-http [18080 {:get {"/hello" {:status 200 :body {:foo 1}}}}]
    @(org.httpkit.client/get "http://localhost:18080/hello"))

  )
