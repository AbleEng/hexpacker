(ns instafoursquare.services
  (:require [instafoursquare.config :refer [foursquare-api instagram-api google-api twitter-creds]]
            [clj-http.client :as client]
            [twitter.api.restful :as twitter]
            [clojure.data.json :as json]
            [throttler.core :refer [fn-throttler throttle-fn]]
            [clojure.string :as string]))

(def foursquare-responses (atom []))
(def instagram-responses (atom []))
(def twitter-responses (atom []))

;;; Create throttled pipelines
(def google-throttled-get (throttle-fn client/get 20 :second))
(def twitter-throttled-search (throttle-fn twitter/search-tweets 720 :hour))
(def instagram-throttled-get (throttle-fn client/get 5000 :hour))

(defn get-google-places-data
  "Given a latitude and a longitude, get the google places data for that location"
  [coords]
  (let [query-params {:location (string/join "," [(:lat coords) (:lng coords)])
                      :radius 50
                      :types "bakery|bar|cafe|food|grocery_or_supermarket|meal_delivery|meal_takeaway|restaurant"
                      :key (:key google-api)}]
    (json/read-str (:body (google-throttled-get (:endpoint google-api) {:query-params query-params})) :key-fn keyword)))

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
                                 (conj current-state (json/read-str (:body (client/gett (:endpoint foursquare-api) {:query-params query-params})) :key-fn keyword))))))

(defn get-instagram-data
  "Given a latitude, longitude, and radius, get the instagram data for that locationxradius and store it in instagram-response"
  [coords]
  (let [query-params {:lat (:lat coords)
                      :lng (:lng coords)
                      :distance 50
                      :access_token (:access_token instagram-api)}]
    (swap! instagram-responses (fn [current-state]
                                 (conj current-state (json/read-str (:body (instagram-throttled-get (:endpoint instagram-api) {:query-params query-params})) :key-fn keyword))))))

(defn get-twitter-data
  "Given a latitude, a longitude, and radius, get the twitter data for that locationxradius and store it in twitter-response"
  [coords]
  (let [params {:geocode (string/join "," [(:lat coords) (:lng coords) "0.05km"])
                :result-type "recent"
                :count 100}]
    (swap! twitter-responses (fn [current-state]
                               (conj current-state (:body (twitter-throttled-search  :oauth-creds twitter-creds :params params)))))))

; (pprint (:resources (:body (twitter/application-rate-limit-status :oauth-creds twitter-creds))))
