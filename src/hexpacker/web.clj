(ns hexpacker.web
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.data.json :as json]
            [ring.adapter.jetty :as ring]
            [ring.middleware.reload :as reload]))

(defn instagram-POST-cb-controller
  "Controller for instagram callback route" 
  [req] 
  (let [body (slurp (:body req))]
    (println (json/read-str body :key-fn keyword)))
  "lol kk thx")

(defn instagram-GET-cb-controller
  "Controller for instagram GET callback route"
  [params]
  (get params "hub.challenge"))

(defroutes app-routes
  (GET "/" [] "<h2>Hello World</h2>")
  (GET "/cb" {params :params} (instagram-GET-cb-controller params))
  (POST "/cb" [] instagram-POST-cb-controller))

(def app
  (handler/site app-routes))

(defn start-server
  "start the server"
  [port]
  (ring/run-jetty app {:port port :join? false}))
