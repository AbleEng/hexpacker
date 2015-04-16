(ns hexpacker.core
  (:gen-class)
  (:require [hexpacker.services :refer [instagram-responses twitter-responses get-google-places-data get-instagram-data get-twitter-data]]
            [hexpacker.web]
            [hexpacker.mercator :refer [wgs84->dmercator dmercator->wgs84]]
            [hexpacker.stitch :refer [min-circles pack-geo-circle]]
            [hexpacker.haversine :refer [haversine]]
            [clojure.string :as string])
  (:use [clojure.tools.nrepl.server :only [start-server stop-server]]))


(println "Defining -main")
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  
  ; We want to search a large radius of 3000m with small radii 50m
  (defonce server (start-server :port 7888))
  (println "Setting constants...")

  (def center-point {:lat 30.268147 :lng -97.743926})
  (def packed-circle-coords (pack-geo-circle center-point 3000 50))
  (def test-list (subvec packed-circle-coords 300 1020))
  (def selected-coords (nth test-list 7))
  
  (let [req-num (count packed-circle-coords)]
    (println (str "Making " req-num " requests to Google, Instagram, and Twitter...")))
  ;;; Make requests & store results (SMALL TEST)
  ;; (println "Getting Google responses...")
  ;; (def google-response (pmap get-google-places-data test-list))
  ;; (println "Getting Instagram responses...")
  ;; (map get-instagram-data test-list)
  ;; (println "Getting Twitter responses...")
  ;; (map get-twitter-data test-list) 

  ;;; Make requests & store results (SCALED TEST)
  (println "Getting Google responses...")
  (def google-response (doall (map get-google-places-data packed-circle-coords)))

  (println "Getting Twitter responses...")
  (doall (map get-twitter-data packed-circle-coords))

  (println "Getting Instagram responses...")
  (doall (map get-instagram-data packed-circle-coords))

  (println "Transforming results...")
  ;;; Transform responses to more workable states
  (def google-response-coords (set (flatten (let [response-results (map :results google-response)]
                                          (for [result response-results]
                                            (for [sub-result result]
                                              {:name (:name sub-result)
                                               :location (:location (:geometry sub-result))}))))))

  (def twitter-statuses (filter (fn [elem]
                                  (if (and 
                                       (not= nil (:lat (:location elem))) 
                                       (not= nil (:lng (:location elem))))
                                    true
                                    false)) 
                                (flatten (let [statuses (flatten (map :statuses @twitter-responses))]
                                           (for [status statuses]
                                             (let [[lat lng] (:coordinates (:geo status))]
                                               {:user (:screen_name (:user status))
                                                :tweet (:text status)
                                                :location {:lat lat :lng lng}}))))))

  (def instagram-media-list
    (map (fn [elem]
           (let [media {:link (:link elem) 
                        :location (:location elem)}]
             media)) (flatten (map :data @instagram-responses))))


;;; Combine the data and order the results
  (def combined-data (for [biz google-response-coords]
                       (let [instagram-results (filter #(not (nil? %1)) (for [media instagram-media-list]
                                                                          (let [lat (:latitude (:location media))
                                                                                lng (:longitude (:location media))
                                                                                ll {:lat lat :lng lng}
                                                                                distance (haversine (:location biz) ll)]
                                                                            (if (< distance 0.015)
                                                                              {:distance distance
                                                                               :link (:link media)}))))
                             twitter-results (filter #(not (nil? %1)) (for [status twitter-statuses]
                                                                        (let [lat (:lat (:location status))
                                                                              lng (:lng (:location status))
                                                                              ll {:lat lat :lng lng}
                                                                              distance (haversine (:location biz) ll)]
                                                                          (if (< distance 0.015)
                                                                            {:distance (haversine (:location biz) ll)
                                                                             :tweet (:tweet status)
                                                                             :user (:user status)}))))
                             total-results (into [] (flatten (conj instagram-results twitter-results)))]
                         (conj biz {:results total-results}))))

  (def ordered-results (reverse (sort-by #(count (:results %1)) combined-data))) 
  ;; (pprint ordered-results)

  ;; For debugging
  (def test-coords 
  (for [coord test-list]
    (let [lat (:lat coord)
          lng (:lng coord)
          stringified (string/join "," [lat lng])]
      stringified)))
  )
