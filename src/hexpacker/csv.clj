(ns hexpacker.csv
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))


(defn write-csv [path row-data]
  (let [columns (vec (keys (first row-data)))
        headers (map name columns)
        rows (mapv #(mapv % columns) row-data)]
    (with-open [file (io/writer path)]
      (csv/write-csv file (cons headers rows)))))
