(ns com.fineshambles.prebuilt.hello
  (:import [com.fineshambles.dexter R]
           [dexter CljActivity]
           [android.app Activity]
           [android.os Bundle])
  (:gen-class
    :extends dexter.CljActivity
    :init init
    :methods [[onCreate [android.os.Bundle] void]]
    )
  )
  
(defn -init [^android.app.Activity activity]
  [[activity] '()])

(defn -onCreate [^dexter.CljActivity this ^android.os.Bundle bundle]
  (-> this (.getActivity) (.setContentView (. com.fineshambles.dexter.R$layout main)))
  (doto (-> this (.getActivity) (.findViewById (. com.fineshambles.dexter.R$id textView)))
    (.setText "Hello clojurized world")))
