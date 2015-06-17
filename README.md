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
    $ java -jar hexpacker-0.1.1-standalone.jar [args]


## Distribution Example

There are 3 main components for dealing with distributed computing with hexpacker: Generator, Worker, and Transformer.

+ `hexpacker.gen/main` generates the lat/long pairs for a given coverage area, chunked into batches of 1000 pairs.

+ `hexpacker.work/main` reads in one such batch and executes (currently google) queries using the lat/long pairs and returns a set of the clean data. Requires an environmental variable `WORKER` to be set to an integer value which denotes the ID of the worker processing the data (depending on how many machines you want to distribute to). This is set up in `config.clj`

+ `hexpacker.transform/main` reads in the accumulated result sets, transforms them, and writes them as csv.

#### Compile & local test example (sub-radius of 15m.)

    $ lein uberjar
    $ java -cp hexpacker-0.1.1-standalone.jar clojure.main -m hexpacker.gen | WORKER=1 java -cp hexpacker-0.1.1-standalone.jar clojure.main -m hexpacker.work 15 | java -cp hexpacker-0.1.1-standalone.jar clojure.main -m hexpacker.transform


## Calling hexpacker from your code

Since this repo is an implementation (as opposed to a library), you will have to load/include the appropriate namespaces manually.

+ `hexpacker.stitch` contains all of the code pertaining to circle packing/coordinate generation. Incanter is also included in this namespace in case you want to run some tests via plotting.

+ `hexpacker.mercator` contains the mercator projection definitions (keep in mind that there are some artifacts in the file for niceness when plotting with incanter)

+ `hexpacker.haversine` contains the haversine (and reverse-haversine) implementations.

+ `hexpacker.core` is the actual 'implementation' for data gathering. You can use it as an example to build out your own cool projects.

Realistically, you should only need to load the `hexpacker.stitch/pack-geo-circle` function if you just want to generate circle packings.

