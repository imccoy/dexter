(ns com.fineshambles.prebuilt.hello
    (:gen-class
      :methods [^:static [world [String] String]]))

(defn -world [thing]
  (str "Hello clojurized world. I like " thing))
