(defproject com.github.igrishaev/any-http "0.1.0-SNAPSHOT"

  :description
  "FIXME: write description"

  :url
  "http://example.com/FIXME"

  :license
  {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
   :url "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies
  []

  :profiles
  {:dev
   {:dependencies
    [[org.clojure/clojure "1.10.1"]

     [clj-http "3.12.0"]
     [http-kit "2.6.0"]
     [aleph "0.6.0"]
     [hato "0.9.0"]
     [org.clj-commons/clj-http-lite "1.0.13"]

     [cheshire "5.10.0"]
     [ring/ring-json "0.5.0"]
     [ring/ring-core "1.7.1"]
     [ring/ring-jetty-adapter "1.7.1"]]}})
