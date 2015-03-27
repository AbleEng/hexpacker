(ns instafoursquare.haversine)

; Haversine formula
; a = sin²(Δφ/2) + cos φ1 ⋅ cos φ2 ⋅ sin²(Δλ/2)
; c = 2 ⋅ atan2( √a, √(1−a) )
; d = R ⋅ c
; where φ is latitude, λ is longitude, R is earth’s radius (mean radius = 6,371km);
; note that angles need to be in radians to pass to trig functions!

(defn haversine
  "Implementation of Haversine formula. Takes two sets of latitude/longitude pairs and returns the shortest great circle distance between them (in km)"
  [{lon1 :lng lat1 :lat} {lon2 :lng lat2 :lat}]
  (let [R 6378.137 ; Radius of Earth in km
        dlat (Math/toRadians (- lat2 lat1))
        dlon (Math/toRadians (- lon2 lon1))
        lat1 (Math/toRadians lat1)
        lat2 (Math/toRadians lat2)
        a (+ (* (Math/sin (/ dlat 2)) (Math/sin (/ dlat 2))) (* (Math/sin (/ dlon 2)) (Math/sin (/ dlon 2)) (Math/cos lat1) (Math/cos lat2)))]
    (* R 2 (Math/asin (Math/sqrt a)))))

; Reverse haversine to derive lat/long from start point, bearing (degrees clockwise from north), & distance (km)
; φ2 = asin( (sin φ1 ⋅ cos δ) + (cos φ1 ⋅ sin δ ⋅ cos θ) )
; λ2 = λ1 + atan2( sin θ ⋅ sin δ ⋅ cos φ1, cos δ − sin φ1 ⋅ sin φ2 )
; where φ is latitude, λ is longitude, θ is the bearing (clockwise from north), δ is the angular distance d/R; d being the distance travelled, R the earth’s radius

(defn reverse-haversine
  "Implementation of the reverse of Haversine formula. Takes one set of latitude/longitude as a start point, a bearing, and a distance, and returns the resultant lat/long pair."
  [{lon :lng lat :lat bearing :bearing distance :distance}]
  (let [R 6378.137 ; Radius of Earth in km
        lat1 (Math/toRadians lat)
        lon1 (Math/toRadians lon)
        angdist (/ distance R)
        theta (Math/toRadians bearing)
        lat2 (Math/toDegrees (Math/asin (+ (* (Math/sin lat1) (Math/cos angdist)) (* (Math/cos lat1) (Math/sin angdist) (Math/cos theta)))))
        lon2 (Math/toDegrees (+ lon1 (Math/atan2 (* (Math/sin theta) (Math/sin angdist) (Math/cos lat1)) (- (Math/cos angdist) (* (Math/sin lat1) (Math/sin lat2))))))]
    {:lat lat2 
     :lng lon2}))