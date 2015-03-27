(ns instafoursquare.core
  (:gen-class)
  (:require [instafoursquare.web :refer [start-server]]
            [instafoursquare.config :refer [foursquare-api instagram-api]]
            [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.string :as string]))

; Scenario 1:
; Get all registered venues in a given area (via shape packing)

; Pipeline:
; Get data -> Stitch -> Transform/Clean -> Result

(def foursquare-response (atom {}))
(def instagram-response (atom {}))

(defn get-foursquare-data 
  "Given a latitude and a longitude, get the foursquare data for that location and store it in foursquare-response"
  [lat lng]
  (let [query-params {:ll (string/join ", " [lat lng])
                      :limit 50
                      :radius 100000
                      :client_id (:client-id foursquare-api)
                      :client_secret (:client-secret foursquare-api)
                      :v (:version foursquare-api)}]
    (swap! foursquare-response (fn [current-state]
                                 (json/read-str (:body (client/get (:endpoint foursquare-api) {:query-params query-params})) :key-fn keyword)))))

(defn get-instagram-data
  "Given a latitude, longitude, and radius, get the instagram data for that locationxradius and store it in instagram-response"
  [lat lng]
  (let [query-params {:lat lat
                      :lng lng
                      :distance 5000
                      :access_token (:access_token instagram-api)}]
    (json/read-str (:body (client/get (:endpoint instagram-api) {:query-params query-params})) :key-fn keyword)))

(defn get-venue-list
  [lat lng]
  (let [foursquare-result (get-foursquare-data lat lng)]
    (map #(list (:name %1) (list (:lat (:location %1)) (:lng (:location %1)))) (:venues (:response foursquare-result)))))

(def suggested-venue (:venue (first (:items (first (:groups (:response foursquare-response)))))))

; (defn compile-data
;   [lat lng]
;   (let [venue-list (get-venue-list lat lng)]
;     (+ 1 1)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (start-server 8080)
  (println "INFO:: Jetty server started. Application up!"))
