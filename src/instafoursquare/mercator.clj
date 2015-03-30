(ns instafoursquare.mercator)

; Spherical Mercator projection algorithm
(def r-major 6378137.0) ; approximated radius of earth

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
  (Math/toDegrees (* r-major (Math/toRadians lng))))

(defn x->lng
  [x]
  (Math/toDegrees (/ (Math/toRadians x) r-major)))

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

; Calculated with respect to earth distances
(defn dy->lat
  [y]
  (Math/toDegrees (- (* 2 (Math/atan (Math/exp (/ y r-major)))) (/ Math/PI 2))))

(defn lat->dy
  [lat]
  (* r-major (Math/log (Math/tan (+ (/ Math/PI 4) (/ (Math/toRadians lat) 2))))))

(defn dx->lng
  [x]
  (Math/toDegrees (/ x r-major)))

(defn lng->dx 
  [lng]
  (* (Math/toRadians lng) r-major))

(defn wgs84->dmercator
  [coordinates]
  (let [{:keys [lat lng]} coordinates]
    {:x (lng->dx lng) :y (lat->dy lat)}))

(defn dmercator->wgs84
  [coordinates]
  (let [{:keys [x y]} coordinates]
    {:lat (dy->lat y) :lng (dx->lng x)}))