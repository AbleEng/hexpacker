(defproject hexpacker "0.1.1-SNAPSHOT"
  :description "A hexagonal circle packing implementation for canvasing/sampling large geographical areas (specifically optimized for Google/Instagram/Twitter APIs)"
  :url "http://example.com/FIXME"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.8"]
                 [clj-http "1.0.1"]
                 [twitter-api "0.7.8"]
                 [ring "1.3.0"]
                 [throttler "1.0.0"]
                 [org.clojure/tools.nrepl "0.2.10"]
                 [org.clojure/data.json "0.2.5"]
                 [org.clojure/data.csv "0.1.2"]
                 [org.slf4j/slf4j-simple "1.6.6"]
                 [incanter "1.9.0"]]
  :plugins [[lein-daemon "0.5.4"]]
  :daemon {:hexpacker {:ns hexpacker.core
                       :pidfile "~/out.pid"}}
  :main ^:skip-aot hexpacker.core
  :target-path "target/%s"
  :profiles {:gen {:main hexpacker.gen
                   :uberjar-name "gen.0.1.1-SNAPSHOT.jar"}
             :work {:main hexpacker.work
                    :uberjar-name "work.0.1.1-SNAPSHOT.jar"}}
  :ring {:handler hexpacker.web/app
         :adapter {:port 8080
                   :join? false}
         :auto-reload? true
         :auto-refresh? true
         :nrepl {:start? true
                 :port 8081}}
  :repl-options {:timeout 900000}
  :jvm-opts ["-Xmx2g"])
  
