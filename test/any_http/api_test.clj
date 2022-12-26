(ns any-http.api-test
  (:require
   [any-http.api :as api]
   [any-http.server :as server]

   [any-http.clj-http :as clj-http]
   [any-http.hato :as hato]
   [any-http.aleph :as aleph]
   [any-http.clj-http-lite :as clj-http-lite]
   [any-http.http-kit :as http-kit]

   [clojure.string :as str]
   [clojure.test :refer [deftest is testing use-fixtures]]))


(def ^:dynamic *client-type* nil)
(def ^:dynamic *client* nil)


(def clients
  {
   :clj-http      clj-http/client
   :clj-http-lite clj-http-lite/client
   :hato          hato/client
   :aleph         aleph/client
   :http-kit      http-kit/client
   })


(defn fix-client [t]
  (doseq [[client-type client] clients]
    (binding [*client-type* client-type
              *client* (client)]
      (testing (format "Testng client: %s" client-type)
        (t)))))


(defmacro when-client-type [client-type & body]
  `(when (= *client-type* ~client-type)
     ~@body))


(defmacro when-not-client-type [client-type & body]
  `(when-not (= *client-type* ~client-type)
     ~@body))


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


(deftest test-post-form-params
  (let [request!
        (atom nil)

        app
        {:post {"/api/post"
                (fn [request]
                  (reset! request! request)
                  {:status 200
                   :body {:ok true}})}}

        {:as resp}
        (server/with-http [38080 app]
          (api/post *client*
                    "http://localhost:38080/api/post"
                    {:content-type :json ;; ignored
                     :form-params {:array [1 2 3]}}))

        request
        @request!]

    (is (str/starts-with?
         (-> request
             :headers
             (get "content-type"))
         "application/x-www-form-urlencoded"))

    (is (= {:array ["1" "2" "3"]}
           (:params request)))))


(deftest test-get-query-params
  (let [params!
        (atom nil)

        app
        {:get {"/api/query"
               (fn [{:keys [params]}]
                 (reset! params! params)
                 {:status 200
                  :body {:ok true}})}}

        {:as resp}
        (server/with-http [38080 app]
          (api/get *client*
                   "http://localhost:38080/api/query"
                   {:content-type :json ;; ignored
                    :query-params {:a 1
                                   :b 2
                                   "foo" {:bar {:baz 3}}
                                   :c [1 2 3]
                                   }}))

        params
        @params!]

    (is (= {:a "1"
            :b "2"
            "foo[bar][baz]" "3"
            :c ["1" "2" "3"]}
           params))))


(deftest test-post-multipart
  (let [params!
        (atom nil)

        app
        {:post {"/api/multipart"
               (fn [{:keys [params]}]
                 (reset! params! params)
                 {:status 200
                  :body {:ok true}})}}

        {:as resp}
        (server/with-http [38080 app]
          (api/post *client*
                   "http://localhost:38080/api/multipart"
                   {:content-type :json ;; ignored
                    :multipart [{:name "title" :content "My Awesome Picture"}
                                {:name "Content/type" :content "image/jpeg"}
                                {:name "foo.txt" :content "Eggplants"}
                                {:name "file" :content (clojure.java.io/file "project.clj")}]}))

        params
        @params!]

    (when-not-client-type :clj-http-lite
      (is (= {:title "My Awesome Picture",
              "Content/type" "image/jpeg",
              "foo.txt" "Eggplants"}
             (dissoc params :file))))

    (when-client-type :clj-http-lite
      (is (= {} params)))))


(deftest test-get-redirects
  )


(deftest test-get-basic-auth
  (let [headers!
        (atom nil)

        app
        {:get {"/api/basic-auth"
               (fn [{:keys [headers]}]
                 (reset! headers! headers)
                 {:status 200
                  :body {:ok true}})}}

        {:as resp}
        (server/with-http [38080 app]
          (api/get *client*
                   "http://localhost:38080/api/basic-auth"
                   {:basic-auth ["username" "password"]}))

        headers
        @headers!]

    (is (= "Basic dXNlcm5hbWU6cGFzc3dvcmQ="
           (get headers "authorization")))))


(deftest test-post-json-str
  )


(deftest test-post-json-stream
  )


(deftest test-get-non-200
  (let [app
        {:get {"/error"
               {:status 500
                :body {:error "oh"}}}}

        {:as resp :keys [status body]}
        (server/with-http [38080 app]
          (api/get *client*
                   "http://localhost:38080/error"))]

    (is (= 500 status))
    (is (instance? java.io.InputStream body))))


(deftest test-timeout
  )


(deftest test-component
  )


(deftest test-kw-headers
  (let [request!
        (atom nil)

        app
        {:get {"/foo"
               (fn [request]
                 (reset! request! request)
                 {:status 200
                  :body {:ok true}})}}

        {:as resp}
        (server/with-http [38080 app]
          (api/get *client*
                   "http://localhost:38080/foo"
                   {:headers {:aaa "foo"
                              "bbb" "bar"}}))

        request
        @request!]

    (is (= {"aaa" "foo" "bbb" "bar"}
           (-> request
               :headers
               (select-keys ["aaa" "bbb"])
               )
           ))

    )

  )
