(ns hexpacker.services
  (:require [hexpacker.config :refer [instagram-api google-api twitter-creds]]
            [clj-http.client :as client]
            [twitter.api.restful :as twitter]
            [clojure.data.json :as json]
            [throttler.core :refer [fn-throttler throttle-fn]]
            [clojure.string :as string]))
;; (println "Defining the atoms")
(def instagram-responses (atom []))
(def twitter-responses (atom []))

;; (println "Creating throttled pipelines")
;;; Create throttled pipelines
(def google-throttled-get (throttle-fn client/get 20 :second))
(def twitter-throttled-search (throttle-fn twitter/search-tweets 10 :minute))
(def instagram-throttled-get (throttle-fn client/get 5000 :hour))


;; (println "Defining get-google-places-data")

;;"bakery|bar|cafe|food|grocery_or_supermarket|meal_delivery|meal_takeaway|restaurant"
(defn get-google-places-data
  "Given a latitude and a longitude, get the google places data for that location"
  [coords]
  (let [query-params {:location (string/join "," [(:lat coords) (:lng coords)])
                      :radius 15
                      :types "accounting|airport|amusement_park|aquarium|art_gallery|atm|bakery|bank|bar|beauty_salon|bicycle_store|book_store|bowling_alley|bus_station|cafe|campground|car_dealer|car_rental|car_repair|car_wash|casino|cemetery|church|city_hall|clothing_store|convenience_store|courthouse|dentist|department_store|doctor|electrician|electronics_store|embassy|establishment|finance|fire_station|florist|food|funeral_home|furniture_store|gas_station|general_contractor|grocery_or_supermarket|gym|hair_care|hardware_store|health|hindu_temple|home_goods_store|hospital|insurance_agency|jewelry_store|laundry|lawyer|library|liquor_store|local_government_office|locksmith|lodging|meal_delivery|meal_takeaway|mosque|movie_rental|movie_theater|moving_company|museum|night_club|painter|park|parking|pet_store|pharmacy|physiotherapist|place_of_worship|plumber|police|post_office|real_estate_agency|restaurant|roofing_contractor|rv_park|school|shoe_store|shopping_mall|spa|stadium|storage|store|subway_station|synagogue|taxi_stand|train_station|travel_agency|university|veterinary_care|zoo"
                      :key (cond
                            (nil? (System/getenv "WORKER")) (:key (:1 google-api))
                            :else (:key ((keyword (System/getenv "WORKER")) google-api)))}]
    (json/read-str (:body (google-throttled-get (cond
                                                 (nil? (System/getenv "WORKER")) (:endpoint (:1 google-api))
                                                 :else (:endpoint ((keyword (System/getenv "WORKER")) google-api))) {:query-params query-params})) :key-fn keyword)))

;; (println "Defining get-instagram-data")
(defn get-instagram-data
  "Given a latitude, longitude, and radius, get the instagram data for that locationxradius and store it in instagram-response"
  [coords]
  (let [query-params {:lat (:lat coords)
                      :lng (:lng coords)
                      :distance 50
                      :access_token (:access_token instagram-api)}]
    (swap! instagram-responses (fn [current-state]
                                 (conj current-state (json/read-str (:body (instagram-throttled-get (:endpoint instagram-api) {:query-params query-params})) :key-fn keyword))))))

;; (println "Defining get-twitter-data")
(defn get-twitter-data
  "Given a latitude, a longitude, and radius, get the twitter data for that locationxradius and store it in twitter-response"
  [coords]
  (let [params {:geocode (string/join "," [(:lat coords) (:lng coords) "0.05km"])
                :result-type "recent"
                :count 100}]
    (swap! twitter-responses (fn [current-state]
                               (conj current-state (:body (twitter-throttled-search  :oauth-creds twitter-creds :params params)))))))

; (pprint (:resources (:body (twitter/application-rate-limit-status :oauth-creds twitter-creds))))
