(ns hexpacker.gen
  (:gen-class)
  (:require [hexpacker.stitch :refer [min-circles pack-geo-circle]]))

(println "Defining Generator -main")
(defn -main
  [lat lng r1 r2 & args]

  (defn string->number [str]
    (let [n (read-string str)]
      (if (number? n) n nil)))
  
  (def center-point {:lat (string->number lat) :lng (string->number lng)})
  (def packed-circle-coords (pack-geo-circle center-point (string->number r1) (string->number r2)))
  ;; (def test-list (subvec packed-circle-coords 300 1020))

  (let [req-num (count packed-circle-coords)]
    (println (str "Total of " req-num " requests needed to cover selected area of radius " r1 "m. with sub-radii " r2 "m.")))

  ;; (println packed-circle-coords)
)
