(ns instafoursquare.core
  (:gen-class)
  (:require [instafoursquare.web :refer [start-server]]
            [instafoursquare.mercator :refer [wgs84->dmercator dmercator->wgs84]]
            [instafoursquare.stitch :refer [min-circles round-pack-circle]]
            [instafoursquare.haversine :refer [haversine]]
            [instafoursquare.config :refer [foursquare-api instagram-api google-api]]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.string :as string]))

; We want to search a large radius of 3000m with small radii 50m

(def foursquare-responses (atom []))
(def instagram-responses (atom []))
(def center-point {:lat 30.268147 :lng -97.743926})
(def center-point-xy (wgs84->dmercator center-point))
(def packed-circle-xy-coords (round-pack-circle 3000 50 center-point-xy))
(def packed-circle-coords (map dmercator->wgs84 packed-circle-xy-coords))
(def test-list (take 5 packed-circle-coords))

(defn get-google-places-data
  "Given a latitude and a longitude, get the google places data for that location"
  [coords]
  (let [query-params {:location (string/join "," [(:lat coords) (:lng coords)])
                      :radius 500
                      :types "airport|bakery|bar|beauty_salon|bicycle_store|book_store|bowling_alley|cafe|campground|car_dealer|car_rental|car_repair|car_wash|casino|clothing_store|convenience_store|dentist|department_store|doctor|electronics_store|establishment|florist|food|furniture_store|gas_station|grocery_or_supermarket|gym|hair_care|hardware_store|health|home_goods_store|insurance_agency|jewelry_store|laundry|library|liquor_store|locksmith|meal_delivery|meal_takeaway|movie_theater|movie_rental|moving_company|night_club|park|pharmacy|plumber|real_estate_agency|restaurant|shopping_mall|spa|store|travel_agency"
                      :key (:key google-api)}]
    (json/read-str (:body (client/get (:endpoint google-api) {:query-params query-params})) :key-fn keyword)))

(defn get-foursquare-data 
  "Given a latitude and a longitude, get the foursquare data for that location and store it in foursquare-response"
  [coords]
  (let [query-params {:ll (string/join "," [(:lat coords) (:lng coords)])
                      :limit 50
                      :radius 50
                      :intent "browse"
                      :client_id (:client-id foursquare-api)
                      :client_secret (:client-secret foursquare-api)
                      :v (:version foursquare-api)}]
    (swap! foursquare-responses (fn [current-state]
                                 (conj current-state (json/read-str (:body (client/get (:endpoint foursquare-api) {:query-params query-params})) :key-fn keyword))))))

(defn get-instagram-data
  "Given a latitude, longitude, and radius, get the instagram data for that locationxradius and store it in instagram-response"
  [coords]
  (let [query-params {:lat (:lat coords)
                      :lng (:lng coords)
                      :distance 50
                      :access_token (:access_token instagram-api)}]
    (swap! instagram-responses (fn [current-state]
                                 (conj current-state (json/read-str (:body (client/get (:endpoint instagram-api) {:query-params query-params})) :key-fn keyword))))))

(def venue-list
  (map (fn [elem]
         (let [venue {:name (:name elem) 
                      :location (:location elem)}]
           venue)) (flatten (map #(:venues (:response %1)) @foursquare-responses))))



(def media-list
  (map (fn [elem]
         (let [media {:link (:link elem) 
                      :location (:location elem)}]
           media)) (flatten (map :data @instagram-responses))))

;({bizcoord1} {bizcoord2} {bizcoord3} {bizcoord4} {bizcoord5})
;({mediacoord1}.......................................{mediacoordn})
;=>

(def google-response (get-google-places-data (nth packed-circle-coords 3)))

(def google-response-coords (map (fn [elem]
                                   {:name (:name elem)
                                    :location (:location (:geometry elem))}) (:results google-response)))

(def combined-data (for [biz google-response-coords]
  (let [results (for [media media-list]
                    (let [lat (:latitude (:location media))
                          lng (:longitude (:location media))
                          ll {:lat lat :lng lng}]
                      {:distance (haversine (:location biz) ll)
                       :link (:link media)}))]
    (conj biz {:results (sort-by :distance results)}))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (start-server 8080)
  (println "INFO:: Jetty server started. Application up!"))
