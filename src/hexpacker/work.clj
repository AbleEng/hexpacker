(ns hexpacker.work
  (:require [hexpacker.services :refer [get-google-places-data]]))


;;; TODO: Thread r2 from gen -> work
;; (println "Defining -main for Worker")
(defn -main
  [& args]

  (def packed-circle-coords (read-string (slurp *in*)))
  
  ;; (let [req-num (count packed-circle-coords)]
  ;;   (println (str "Making " req-num " requests to Google, Instagram, and Twitter...")))
  ;; (println "Getting Google responses...")
  (def google-response (doall (map get-google-places-data packed-circle-coords)))

  ;; (println "Transforming results...")

  (def google-places-cleaned (set (flatten (let [response-results (map :results google-response)]
                                             (for [result response-results]
                                               (for [sub-result result]
                                                 {:name (:name sub-result)
                                                  :place_id (:place_id sub-result)
                                                  :types (:types sub-result)}))))))
  ;; (println "Doing something with the data")
  (println google-places-cleaned)
  )
