(ns any-http.util
  (:refer-clojure :exclude [update-keys parse-long])
  (:import java.util.Base64)
  (:require
   [clojure.string :as str]))


(defn b64-decode ^bytes [^bytes input]
  (.decode (Base64/getDecoder) input))


(defn b64-encode ^bytes [^bytes input]
  (.encode (Base64/getEncoder) input))


(defn bytes->str
  (^String [^bytes input]
   (new String input))

  (^String [^bytes input ^String encoding]
   (new String input encoding)))


(defn str->bytes
  (^bytes [^String input]
   (.getBytes input))

  (^bytes [^String input ^String encoding]
   (.getBytes input encoding)))


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
