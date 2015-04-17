# hexpacker

A hexagonal circle packing implementation for canvasing/sampling large geographical areas (specifically optimized for Google/Instagram/Twitter APIs)

## Installation

    $ git clone git@github.com:shayanjm/hexpacker.git

## Usage

#### To play:

    $ lein repl

#### To work:

    $ lein uberjar
    $ java -jar hexpacker-0.1.0-standalone.jar [args]


## Examples

If you wanted to generate the collection of packed circles bounded by a large radius of 3KM, and smaller radii of 50 meters:

```clojure
(ns hexpacker.stitch)
(def center-point {:lat 30.268147 :lng -97.743926})
(def packed-circle-coords (pack-geo-circle center-point 3000 50))
(count packed-circle-coords)
;=> 2791
```
This repo hasn't been "library-ized" just yet, but I may spin out the mercator, haversine, and stitching components into separate modules.


## Calling hexpacker from your code

Since this repo is an implementation (as opposed to a library), you will have to load/include the appropriate namespaces manually.

+ `hexpacker.stitch` contains all of the code pertaining to circle packing/coordinate generation. Incanter is also included in this namespace in case you want to run some tests via plotting.

+ `hexpacker.mercator` contains the mercator projection definitions (keep in mind that there are some artifacts in the file for niceness when plotting with incanter)

+ `hexpacker.haversine` contains the haversine (and reverse-haversine) implementations.

Realistically, you should only need to load hexpacker.stitch/pack-geo-circle function if you just want to generate circle packings.
### Notes

+ 

+ Using spherical approximation of earth (versus spheroid) because it's a simpler model. It introduces some error, but who cares?

+ Consistent with the same assumptions used by most consumer-level GIS services (i.e: Google).

+ Using mercator projection to overlay 2D mesh (circle packing) onto 3D surface (Earth)
+ Distance from point < 15m = match.
    * Potbelly @ 30.286843,-97.741953
    * https://instagram.com/p/08Q3mqmziS/ @ 0.0128km away


## License
The MIT License (MIT)

Copyright © 2015 Shayan Mohanty

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
