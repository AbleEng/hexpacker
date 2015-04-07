(defproject instafoursquare "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.8"]
                 [clj-http "1.0.1"]
                 [twitter-api "0.7.8"]
                 [ring "1.3.0"]
                 [throttler "1.0.0"]
                 [org.clojure/data.json "0.2.5"]
                 [incanter "1.9.0"]]
  :main ^:skip-aot instafoursquare.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :ring {:handler instafoursquare.web/app
         :adapter {:port 8080
                   :join? false}
         :auto-reload? true
         :auto-refresh? true
         :nrepl {:start? true
                 :port 8081}}
  :repl-options {:timeout 900000})
  
