(ns hexpacker.transform
  (:require [clojure.string :as s]
            [hexpacker.csv :refer [write-csv echo-csv]]))

;; (println "Defining -main for Transform")
(defn -main
  [& args]
  
  (def input-raw (slurp *in*))
  (let [input-sets (map read-string (s/split-lines input-raw))
        cleaned-input (reduce #{} input-sets)]
    (println (str "Writing " (count input-sets) " results to /tmp/google-results.csv ..."))
    (time (write-csv "/tmp/google-results.csv" cleaned-input))
  ))
