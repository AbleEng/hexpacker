(ns hexpacker.stitch
  (:require [hexpacker.mercator :refer [wgs84->mercator wgs84->dmercator mercator->wgs84 dmercator->wgs84]]
            [hexpacker.haversine :refer [haversine reverse-haversine]])
  (:use [incanter core charts]))

(defn min-circles
  "Determines the minimum number of circles of radius r2 that fit inside a circle of radius r1 assuming: A hexagonal configuration, no overlap, the first circle of r2 at the center of the circle of r1, and a 2D plane."
  [r1 r2]
  (let [layer-lim (+ 1 (long (/ r1 (* 2 r2))))
        circles-by-layer (for [l (range layer-lim)] (* 6 l))]
    (+ 1 (sum circles-by-layer))))

(defn calculate-xy-coords
  "Given a start point, a distance, and a bearing (in degrees from north), determine the resultant x-y coordinates"
  [{x :x
    y :y 
    distance :distance
    bearing  :bearing}]
  (let [angle (Math/toRadians bearing)
        dx (* distance (Math/cos angle))
        dy (* distance (Math/sin angle))]
    {:x (+ dx x) 
     :y (+ dy y)}))

(defmacro next-layer-fn
  "Generates a function which, given a vector of hashmaps containing x-y coordinates of a layer of circles forming 'Pascals Triangle', returns the next layer."
  [r2]
  `(fn [previous-layer#]
     (into []
           (set (flatten
                 (map (fn [elem#]
                        (let [circle1-xy-coords# (calculate-xy-coords {:x (:x elem#)
                                                                      :y (:y elem#)
                                                                      :distance (* 2 ~r2)
                                                                      :bearing 0})
                              circle2-xy-coords# (calculate-xy-coords {:x (:x elem#)
                                                                      :y (:y elem#)
                                                                      :distance (* 2 ~r2)
                                                                      :bearing 60})]
                          [circle1-xy-coords# circle2-xy-coords#])) previous-layer#))))))


(defn pascals-triangle
  "Given a start point and total number of layers, returns a 'pascals-triangle' configuration of circles"
  [start-point layers r2]
  (take layers (iterate (next-layer-fn r2) [start-point])))

(defn rotate-about
  "Given two points and an angle of rotation, return the resultant x-y pair for rotating A about B."
  [pta ptb angle-degrees]
  (let [angle (Math/toRadians angle-degrees)
        a (:x ptb)
        b (:y ptb)
        x (- (:x pta) a)
        y (- (:y pta) b)
        newx (+ (- (* x (Math/cos angle)) (* y (Math/sin angle))) a)
        newy (+ (+ (* x (Math/sin angle)) (* y (Math/cos angle))) b)]
    {:x newx
     :y newy}))

(defn pack-circle
  "Generates vector of x-y coordinates for circle-in-circle hexagonal packing for circles of radii r1 and r2 where r1 > r2."
  [r1 r2 center]
  (set (flatten (let [num-layers (+ 1 (long (/ r1 (* 2 r2))))
                      flat-pascals-triangle (flatten (pascals-triangle center num-layers r2))]
                  (take 6 (iterate (fn [flat-triangle]
                                     (map #(rotate-about %1 center 60) flat-triangle)) flat-pascals-triangle))))))

(defn round-pack-circle
  "Generates rounded values for x-y coordinates from pack-circle. Center should be provided as a hashmap of format {:x x :y y}"
  [r1 r2 center]
  (let [packed-circle-coords (pack-circle r1 r2 center)]
    (set (map (fn [{x :x
                y :y}]
       (let [roundx (read-string (format "%.6f" x))
             roundy (read-string (format "%.6f" y))]
         {:x roundx :y roundy})) packed-circle-coords))))

(defn pack-geo-circle
  "Given a latitude/longitude center point, a large bounding area of radius r1, and a smaller radius r2, generate the lat/long coordinates of all of the circles of r2 that pack the area of r1."
  [center-point r1 r2]
  (let [center-point-xy (wgs84->dmercator center-point)
        packed-circle-xy-coords (round-pack-circle r1 r2 center-point-xy)
        packed-circle-coords (into [] (map dmercator->wgs84 packed-circle-xy-coords))]
    packed-circle-coords))


(defmacro circle
  "Returns a function for a circle given h (x offset), k (y offset), and r (radius)."
  [h k r]
  `(fn [t#] [(+ ~h (* ~r (cos t#))) (+ ~k (* ~r (sin t#)))]))

(def austin-circle
  (let [rad 3000
        lat 30.2500 
        lng 97.7500
        mapping (wgs84->mercator {:lat lat :lng lng})
        x (:x mapping)
        y (:y mapping)]
    (circle x y rad)))

(def big-circle
  (let [rad 5000000
        lat 30.2500 
        lng 97.7500
        mapping (wgs84->mercator {:lat lat :lng lng})
        x (:x mapping)
        y (:y mapping)]
    (circle x y rad)))

(def query-circle 
  (let [rad 500
        lat 30.2500
        lng 97.7500
        mapping (wgs84->mercator {:lat lat :lng lng})
        x (:x mapping)
        y (:y mapping)]
    (circle x y rad)))

(def plot (let [p (parametric-plot austin-circle (- Math/PI) Math/PI
                           :title "Circle Packing"
                           :x-label "Distance X"
                           :y-label "Distance Y")]
            p))
; For plotting:
;; (def center-point {:lat 30.2500  :lng 97.7500})
;; (def center-point-xy (wgs84->mercator center-point))
;; (def packed-circle-coords (round-pack-circle 3000 50 center-point-xy))
;; (time (doall (map #(add-parametric plot (circle (:x %1) (:y %1) 50) Math/PI (- Math/PI)) packed-circle-coords)))

;; (view plot)
