(ns any-http.util
  (:require
   [clojure.string :as str]))


(defn update-keys [m f]
  (persistent!
   (reduce-kv
    (fn [acc! k v]
      (assoc! acc! (f k) v))
    (transient {})
    m)))


(defn header->kw [header]
  (-> header str/lower-case keyword))


(defmacro as
  {:style/indent 1}
  [val [bind] & body]
  `(let [~bind ~val]
     ~@body))
