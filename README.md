# Pubchess Scalatra #
A scalatra, scala, mongoDB based webapp for
* managing players
* creating tournaments
* drawing fair(!) tournaments (single or double)
* saving internal elo for each player
* showing simple graphics for players elo rating
Has no
* pretty front-end
* authentication, authorization

## Build & Run ##
Install mongoDB
Run mongoDB

```sh
$ cd pubchess-scalatra
$ ./sbt
> container:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.

Also possible to use revolver for fast recompile of deployed code while developing:
```sh
$ cd pubchess-scalatra
$ ./sbt
> ~re-start
> browse
```
If `browse` doesn't launch your browser, manually open [http://localhost:7002/](http://localhost:7002/) in your browser.
