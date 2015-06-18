(ns hexpacker.config
  (:require [twitter.oauth :as oauth]))


(def instagram-api {:access_token "INSTAGRAM ACCESS TOKEN"
                    :client_id "INSTAGRAM CLIENT ID"
                    :endpoint "https://api.instagram.com/v1/media/search"})

(def google-api {:1 {:key "GOOGLE API KEY1"
                     :endpoint "https://maps.googleapis.com/maps/api/place/nearbysearch/json"}
                 :2 {:key "GOOGLE API KEY2"
                     :endpoint "https://maps.googleapis.com/maps/api/place/nearbysearch/json"}})

(def twitter-creds (oauth/make-oauth-creds "TWITTER CONSUMER KEY"
                                           "TWITTER CONSUMER SECRET"
                                           "TWITTER USER ACCESS TOKEN"
                                           "TWITTER USER ACCESS TOKEN SECRET"))
