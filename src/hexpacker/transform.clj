(ns hexpacker.transform
  (:require [clojure.string :as s]
            [hexpacker.csv :refer [write-csv echo-csv]]))

;; (println "Defining -main for Transform")
(defn -main
  [timestamped-name & args]
  
  (def input-raw (slurp *in*))
  (let [input-sets (map #(read-string (read-string %)) (s/split-lines input-raw))
        cleaned-input (reduce into input-sets)]
    (println (str "Writing " (count cleaned-input) " results to /tmp/hexpacker/results/" timestamped-name ".csv..."))
    (time (write-csv (str "/tmp/hexpacker/results/" timestamped-name ".csv") cleaned-input))
  ))
