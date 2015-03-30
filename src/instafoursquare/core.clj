(ns instafoursquare.core
  (:gen-class)
  (:require [instafoursquare.web :refer [start-server]]
            [instafoursquare.mercator :refer [wgs84->mercator mercator->wgs84]]
            [instafoursquare.stitch :refer [min-circles round-pack-circle]]
            [instafoursquare.config :refer [foursquare-api instagram-api]]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.string :as string]))

; We want to search a large radius of 3000m with small radii 50m

(def foursquare-responses (atom []))
(def instagram-responses (atom []))
(def center-point {:lat 30.268147 :lng -97.743926})
(def center-point-xy (wgs84->mercator center-point))

(defn get-foursquare-data 
  "Given a latitude and a longitude, get the foursquare data for that location and store it in foursquare-response"
  [lat lng]
  (let [query-params {:ll (string/join "," [lat lng])
                      :limit 50
                      :radius 50
                      :intent "browse"
                      :client_id (:client-id foursquare-api)
                      :client_secret (:client-secret foursquare-api)
                      :v (:version foursquare-api)}]
    (swap! foursquare-responses (fn [current-state]
                                 (json/read-str (:body (client/get (:endpoint foursquare-api) {:query-params query-params})) :key-fn keyword)))))

(defn get-instagram-data
  "Given a latitude, longitude, and radius, get the instagram data for that locationxradius and store it in instagram-response"
  [coords]
  (let [query-params {:lat (:lat coords)
                      :lng (:lng coords)
                      :distance 50
                      :access_token (:access_token instagram-api)}]
    (swap! instagram-responses (fn [current-state]
                                 (json/read-str (:body (client/get (:endpoint instagram-api) {:query-params query-params})) :key-fn keyword)))))



; (defn compile-data
;   [lat lng]
;   (let [venue-list (get-venue-list lat lng)]
;     (+ 1 1)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (start-server 8080)
  (println "INFO:: Jetty server started. Application up!"))
