# hexpacker

A hexagonal circle packing implementation for canvasing/sampling large geographical areas (specifically optimized for Google/Instagram/Twitter APIs)

## Installation

   $ git clone git@github.com:shayanjm/hexpacker.git

## Usage

#### To play:

    $ lein repl

#### To work:

    $ java -jar hexpacker-0.1.0-standalone.jar [args]


## Examples

If you wanted to generate the collection of packed circles bounded by a large radius of 3KM, and smaller radii of 50 meters:
   (def center-point {:lat 30.268147 :lng -97.743926})
   (def packed-circle-coords (pack-geo-circle center-point 3000 50))
   (count packed-circle-coords)
   ;=> 2791

### Notes

+ Using spherical approximation of earth (versus spheroid) because it's a simpler model. It introduces some error, but who cares?

+ Consistent with the same assumptions used by most consumer-level GIS services (i.e: Google).

+ Using mercator projection to overlay 2D mesh (circle packing) onto 3D surface (Earth)
+ Distance from point < 15m = match.
    * Potbelly @ 30.286843,-97.741953
    * https://instagram.com/p/08Q3mqmziS/ @ 0.0128km away


### TODO


## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
