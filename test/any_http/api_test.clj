(ns any-http.api-test
  (:require
   [any-http.api :as api]
   [any-http.server :as server]

   [any-http.clj-http :as clj-http]
   [any-http.hato :as hato]
   [any-http.aleph :as aleph]
   [any-http.clj-http-lite :as clj-http-lite]
   [any-http.http-kit :as http-kit]

   [clojure.test :refer [deftest is testing use-fixtures]]))


(def ^:dynamic *client* nil)


(def clients
  [["clj-http" clj-http/client]
   ["clj-http-lite" clj-http-lite/client]
   ["hato" hato/client]
   ["aleph" aleph/client]
   ["http-kit" http-kit/client]])


(defn fix-client [t]
  (doseq [[c-name c-new] clients]
    (binding [*client* (c-new)]
      (testing (format "Testng client: %s" c-name)
        (t)))))


(use-fixtures :each fix-client)


(deftest test-get
  (let [app
        {:get {"/api/get" {:status 200 :body {:foo 1}}}}

        resp
        (server/with-http [38080 app]
          (api/get *client* "http://localhost:38080/api/get"))]

    (is (= {:status 200
            :headers
            {:connection "close",
             :content-type "application/json;charset=utf-8",
             :server "Jetty(9.4.12.v20180830)"}}

           (-> resp
               (dissoc :body)
               (update :headers dissoc :date))))))
