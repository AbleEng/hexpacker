(ns hexpacker.transform
  (:require [clojure.string :as s]
            [hexpacker.csv :refer [write-csv]]))

;; (println "Defining -main for Transform")
(defn -main
  [& args]

  (def input-sets (s/split-lines (slurp *in*)))

  (let [input-sets (map read-string (s/split-lines (slurp *in*)))
        total-set (set (map #(into {} %) input-sets))]
    (println (str "Writing " (count total-set) " results to /tmp/google-results.csv ..."))
    (time (write-csv "/tmp/google-results.csv" total-set)))

  (println "Finished"))
