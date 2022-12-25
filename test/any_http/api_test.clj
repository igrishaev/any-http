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
  {
   "clj-http"      clj-http/client
   "clj-http-lite" clj-http-lite/client
   "hato"          hato/client
   "aleph"         aleph/client
   "http-kit"      http-kit/client
   })


(defn fix-client [t]
  (doseq [[c-name c-new] clients]
    (binding [*client* (c-new)]
      (testing (format "Testng client: %s" c-name)
        (t)))))


(use-fixtures :each fix-client)


(deftest test-get
  (let [app
        {:get {"/api/get" {:status 200 :body {:foo 1}}}}

        {:as resp :keys [body]}
        (server/with-http [38080 app]
          (api/get *client* "http://localhost:38080/api/get"))]

    (is (= {:status 200
            :headers
            {:content-type "application/json;charset=utf-8",
             :server "Jetty(9.4.12.v20180830)"}}

           (-> resp
               (dissoc :body)
               (update :headers select-keys [:content-type :server]))))

    (is (api/ok? resp))
    (is (api/json? resp))))


(deftest test-post-json
  (let [capture!
        (atom nil)

        app
        {:post {"/api/post"
                (fn [{:as request :keys [params]}]
                  (reset! capture! request)
                  {:status 200
                   :body {:input params}})}}

        {:as resp}
        (server/with-http [38080 app]
          (api/post *client*
                    "http://localhost:38080/api/post"

                    {:content-type :json
                     :form-params {:array [1 2 3]}}

                    #_
                    {:headers {"content-type" "application/json"}
                     :body (cheshire.core/generate-string {:a 1 :b 2})}))]

    #_
    (is (= {:array [1 2 3]}
           (-> @capture! :params)))

    (is (= "application/json"
           (-> @capture! :headers (get "content-type"))))



    #_
    (is (= 1 resp))



    )

  )
