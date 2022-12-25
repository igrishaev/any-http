(ns any-http.util
  (:refer-clojure :exclude [update-keys parse-long])
  (:require
   [clojure.string :as str]))


(defn update-keys [m f]
  (persistent!
   (reduce-kv
    (fn [acc! k v]
      (assoc! acc! (f k) v))
    (transient {})
    m)))


(defn parse-long [value]
  (try
    (Long/parseLong value)
    (catch NumberFormatException e
      nil)))


(defn header->kw [header]
  (-> header str/lower-case keyword))


(defmacro as
  {:style/indent 1}
  [val [bind] & body]
  `(let [~bind ~val]
     ~@body))
