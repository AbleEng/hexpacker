(ns hexpacker.transform
  (:require [clojure.string :as s]
            [hexpacker.csv :refer [write-csv echo-csv]]))

;; (println "Defining -main for Transform")
(defn -main
  [& args]
  
  (def input-raw (slurp *in*))
  ;; (prn input-raw))

  ;; (let [input-sets (map read-string (s/split-lines input-raw))]
  ;;   (prn input-sets))
  ;; )

  (let [input-sets (map read-string (s/split-lines input-raw))]
    (println (str "Writing " (count input-sets) " results to /tmp/google-results.csv ..."))
    (time (write-csv "/tmp/google-results.csv" input-sets))
    ;; (prn input-sets)
  ))
