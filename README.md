# hexpacker

A hexagonal circle packing implementation for canvasing/sampling large geographical areas (specifically optimized for Google/Instagram/Twitter APIs)

![screenshot](https://camo.githubusercontent.com/0ee61a5173d3e8e0103b544c6711857510fa2dc1/687474703a2f2f7075752e73682f68676a56762f393839386238336361332e706e67)

## Installation

    $ git clone git@github.com:shayanjm/hexpacker.git

## Usage

#### To play:

    $ lein repl

#### To work:

    $ lein uberjar
    $ java -jar hexpacker-0.1.0-standalone.jar [args]


## Example

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

+ `hexpacker.core` is the actual 'implementation' for data gathering. You can use it as an example to build out your own cool projects.

Realistically, you should only need to load the `hexpacker.stitch/pack-geo-circle` function if you just want to generate circle packings.

