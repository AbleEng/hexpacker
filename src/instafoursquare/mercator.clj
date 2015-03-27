(ns instafoursquare.mercator)

; Spherical Mercator projection algorithm
(defn lat->y
  [lat]
  (let [alat (+
               (/ Math/PI 4)
               (/ (Math/toRadians lat) 2))]
    (-> alat
        Math/tan
        Math/log
        Math/toDegrees)))

(defn lng->x
  [lng]
  (* lng 2))

(defn x->lng
  [x]
  (/ x 2))

(defn y->lat
  [y]
  (let [ay (-> y
               Math/toRadians
               Math/exp
               Math/atan
               (* 2))]
    (Math/toDegrees 
      (- ay (/ Math/PI 2)))))

(defn wgs84->mercator
  [coordinates]
  (let [{:keys [lat lng]} coordinates]
    {:x (lng->x lng) :y (lat->y lat)}))

(defn mercator->wgs84
  [coordinates]
  (let [{:keys [x y]} coordinates]
    {:lat (y->lat y) :lng (x->lng x)}))