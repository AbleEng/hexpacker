# Instafoursquare

Combine Instagram & Foursquare data to determine the most popular businesses in a given area.

## Installation

TODO

## Usage

FIXME: explanation

    $ java -jar instafoursquare-0.1.0-standalone.jar [args]


## Examples

...

### Notes

+ Using spherical approximation of earth (versus spheroid) because it's a simpler model. It introduces some error, but who cares?

+ Treating bounding circles as mapped to 2D instead of 3D. If 3D: http://en.wikipedia.org/wiki/Thomson_problem
+ Distance from point < 15m = match.
    * Potbelly @ 30.286843,-97.741953
    * https://instagram.com/p/08Q3mqmziS/ @ 0.0128km away


### TODO


## License

Copyright © 2015 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
