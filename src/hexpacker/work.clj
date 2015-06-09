(ns hexpacker.work
  (:require [hexpacker.services :refer [get-google-places-data]]))

(println "Defining -main for Worker")
(defn -main
  [& args]

  (def packed-circle-coords) (line-seq (java.io.BufferedReader. *in*))
  
  )
