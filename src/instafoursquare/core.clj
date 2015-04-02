(ns instafoursquare.core
  (:gen-class)
  (:require [instafoursquare.services :refer [instagram-responses twitter-responses get-google-places-data get-instagram-data get-twitter-data]]
            [instafoursquare.web :refer [start-server]]
            [instafoursquare.mercator :refer [wgs84->dmercator dmercator->wgs84]]
            [instafoursquare.stitch :refer [min-circles round-pack-circle]]
            [instafoursquare.haversine :refer [haversine]]
            [clojure.string :as string]))

; We want to search a large radius of 3000m with small radii 50m

(def center-point {:lat 30.268147 :lng -97.743926})
(def center-point-xy (wgs84->dmercator center-point))
(def packed-circle-xy-coords (round-pack-circle 3000 50 center-point-xy))
(def packed-circle-coords (into [] (map dmercator->wgs84 packed-circle-xy-coords)))
; (def test-list (subvec packed-circle-coords 360 370))
(def test-list (subvec packed-circle-coords 300 480))
(def selected-coords (nth test-list 7))


;({bizcoord1} {bizcoord2} {bizcoord3} {bizcoord4} {bizcoord5})
;({mediacoord1}.......................................{mediacoordn})
;=>

(def google-response (doall (pmap get-google-places-data test-list)))
(doall (pmap get-instagram-data test-list))
(doall (pmap get-twitter-data test-list))

(def google-response-coords (flatten (let [response-results (map :results google-response)]
                              (for [result response-results]
                                (for [sub-result result]
                                  {:name (:name sub-result)
                                   :location (:location (:geometry sub-result))})))))

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

(def media-list
  (map (fn [elem]
         (let [media {:link (:link elem) 
                      :location (:location elem)}]
           media)) (flatten (map :data @instagram-responses))))


(def combined-data (for [biz google-response-coords]
                     (let [instagram-results (filter #(not (nil? %1)) (for [media media-list]
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
(pprint ordered-results)

; (:name (nth ordered-results 0))

; (count (:results (nth ordered-results 1)))

; (map :name ordered-results)

(def test-coords 
  (for [coord test-list]
    (let [lat (:lat coord)
          lng (:lng coord)
          stringified (string/join "," [lat lng])]
      stringified)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (start-server 8080)
  (println "INFO:: Jetty server started. Application up!"))
