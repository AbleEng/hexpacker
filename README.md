# Instafoursquare

Using circle-in-circle-on-sphere packing to canvas large geographical areas and discern interesting facts at macro scales.

## Installation

TODO

## Usage

#### To play:

    $ lein repl

#### To work:

    $ java -jar instafoursquare-0.1.0-standalone.jar [args]


## Examples

...

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
